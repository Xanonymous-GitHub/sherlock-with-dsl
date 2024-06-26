package uk.ac.warwick.dcs.sherlock.engine;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;
import uk.ac.warwick.dcs.sherlock.api.event.EventInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPostInitialisation;
import uk.ac.warwick.dcs.sherlock.api.event.EventPreInitialisation;
import uk.ac.warwick.dcs.sherlock.api.executor.IExecutor;
import uk.ac.warwick.dcs.sherlock.api.model.detection.DetectionType;
import uk.ac.warwick.dcs.sherlock.api.registry.SherlockRegistry;
import uk.ac.warwick.dcs.sherlock.api.storage.IStorageWrapper;
import uk.ac.warwick.dcs.sherlock.api.util.SherlockHelper;
import uk.ac.warwick.dcs.sherlock.api.util.Side;
import uk.ac.warwick.dcs.sherlock.engine.executor.BaseExecutor;
import uk.ac.warwick.dcs.sherlock.engine.storage.BaseStorage;

import java.io.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

/**
 * Main engine class, creates a new instance of Sherlock
 */
public class SherlockEngine {

    public static final String version = "@VERSION@";
    private static final Logger logger = LoggerFactory.getLogger(SherlockEngine.class);
    public static Side side = Side.UNKNOWN;
    public static Configuration configuration = null;
    public static IStorageWrapper storage = null;
    public static IExecutor executor = null;
    public static URLClassLoader classloader;
    static EventBus eventBus = null;
    static Registry registry = null;
    static String overrideModulesPath = "";
    static File configDir;
    private final File lockFile;
    private FileChannel lockChannel;
    private FileLock lock;
    private boolean valid;
    private boolean initialised = false;

    /**
     * Initial setup
     *
     * @param side Client or Server
     */
    public SherlockEngine(Side side) {
        SherlockEngine.classloader = new URLClassLoader(new URL[0], this.getClass().getClassLoader()); // Custom classloader for the modules

        this.valid = false;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        SherlockEngine.side = side;

        SherlockEngine.setupConfigDir();

        this.lockFile = new File(SherlockEngine.configDir.getAbsolutePath() + File.separator + "Sherlock.lock");
        try {
            RandomAccessFile f = new RandomAccessFile(lockFile, "rw");
            this.lockChannel = f.getChannel();
            try {
                this.lock = this.lockChannel.tryLock();

                if (this.lock != null) {
                    this.valid = true;
                }
            } catch (OverlappingFileLockException e) {
                System.out.println("Overlap");
                //this.valid = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (this.valid) {
            SherlockEngine.loadConfiguration();

            try {
                SherlockEngine.eventBus = new EventBus();
                Field field = uk.ac.warwick.dcs.sherlock.api.event.EventBus.class.getDeclaredField("bus");
                field.setAccessible(true);
                field.set(null, SherlockEngine.eventBus);

                SherlockEngine.registry = new Registry();
                field = SherlockRegistry.class.getDeclaredField("registry");
                field.setAccessible(true);
                field.set(null, SherlockEngine.registry);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Test the presence of a module at the passed classpath, should point to a class within the module
     *
     * @param classpath the module classpath as a string (to avoid requiring an import). Example for base Sherlock module: "uk.ac.warwick.dcs.sherlock.module.model.base.ModuleModelBase"
     * @return Whether the module is present
     */
    public static boolean isModulePresent(String classpath) {
        try {
            Class.forName(classpath, true, SherlockEngine.classloader);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Load the config file from the user directory
     */
    private static void loadConfiguration() {
        File configFile = new File(SherlockEngine.configDir.getAbsolutePath() + File.separator + "Sherlock.yaml");
        if (!configFile.exists()) {
            SherlockEngine.configuration = new Configuration();
            SherlockEngine.writeConfiguration(configFile);
        } else {
            try {
                Constructor constructor = new Constructor(new LoaderOptions());
                constructor.addTypeDescription(new TypeDescription(Configuration.class, "!Sherlock"));
                Yaml yaml = new Yaml(constructor);
                SherlockEngine.configuration = yaml.loadAs(new FileInputStream(configFile), Configuration.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            SherlockEngine.writeConfiguration(configFile);
        }
    }

    /**
     * Set the module override location
     *
     * @param overrideModulesPath new path for modules
     */
    public static void setOverrideModulesPath(String overrideModulesPath) {
        SherlockEngine.overrideModulesPath = overrideModulesPath;
    }

    /**
     * Setup the dir locations
     */
    private static void setupConfigDir() {
        SherlockEngine.configDir = new File(SystemUtils.IS_OS_WINDOWS ? System.getenv("APPDATA") + File.separator + "Sherlock" : System.getProperty("user.home") + File.separator + ".Sherlock");

        if (!SherlockEngine.configDir.exists()) {
            if (!SherlockEngine.configDir.mkdir()) {
                logger.error("Could not create dir: {}", SherlockEngine.configDir.getAbsolutePath());
                System.exit(1);
            }
        }
    }

    /**
     * Write the config file to the disk
     *
     * @param configFile file to write to
     */
    private static void writeConfiguration(File configFile) {
        try {
            Representer representer = new Representer(new DumperOptions());
            representer.addClassTag(Configuration.class, new Tag("!Sherlock"));
            DumperOptions options = new DumperOptions();
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(representer, options);
            FileWriter writer = new FileWriter(configFile);
            yaml.dump(SherlockEngine.configuration, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialise Sherlock!, must be called for Sherlock to work
     */
    public void initialise() {
        if (!this.valid) {
            logger.error("Cannot initialise SherlockEngine, is not valid. Likely an instance of Sherlock is already running");
            System.exit(1);
        }

        logger.info("Starting SherlockEngine on Side.{}", side.name());

        SherlockEngine.storage = new BaseStorage(); //expand to choose wrappers
        SherlockEngine.executor = new BaseExecutor(); //expand to choose wrappers

        try {
            Field field = SherlockHelper.class.getDeclaredField("sourceFileHelper");
            field.setAccessible(true);
            field.set(null, SherlockEngine.storage);

            field = SherlockHelper.class.getDeclaredField("codeBlockGroupClass");
            field.setAccessible(true);
            field.set(null, SherlockEngine.storage.getCodeBlockGroupClass());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error("Could not set processed results class", e);
        }

        AnnotationLoader modules = new AnnotationLoader();
        modules.registerModules();

        DetectionType.addDefaultDetectionTypes();
        SherlockEngine.eventBus.publishEvent(new EventPreInitialisation());
        SherlockEngine.eventBus.publishEvent(new EventInitialisation());
        SherlockEngine.registry.loadDetectionTypeWeights();
        SherlockEngine.registry.analyseDetectors();
        SherlockEngine.eventBus.publishEvent(new EventPostInitialisation());

        //Cleanup init events, we don't need them any more
        SherlockEngine.eventBus.removeInvocationsOfEvent(EventPreInitialisation.class);
        SherlockEngine.eventBus.removeInvocationsOfEvent(EventInitialisation.class);
        SherlockEngine.eventBus.removeInvocationsOfEvent(EventPostInitialisation.class);

        this.initialised = true;
    }

    /**
     * is SherlockEngine initialised
     *
     * @return is SherlockEngine initialised
     */
    public boolean isInitialised() {
        return initialised;
    }

    /**
     * is SherlockEngine valid
     *
     * @return is valid
     */
    public boolean isValidInstance() {
        return this.valid;
    }

    /**
     * Shutdown hook
     */
    private void shutdown() {
        logger.info("Stopping SherlockEngine");
        try {
            if (SherlockEngine.storage != null) {
                SherlockEngine.storage.close();
            }
            if (SherlockEngine.executor != null) {
                SherlockEngine.executor.shutdown();
            }

            if (this.lock != null) {
                this.lock.close();
            }
            if (this.lockChannel != null) {
                this.lockChannel.close();
            }
            if (this.lockFile != null) {
                this.lockFile.delete();
            }
        } catch (Exception ignored) {
        }
    }
}
