package uk.ac.warwick.dcs.sherlock.module.model.base.detection

import tw.xcc.gumtree.antlrBridge.GumTreeConverter
import tw.xcc.gumtree.model.GumTree
import uk.ac.warwick.dcs.sherlock.api.component.ISourceFile
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.CPP14Lexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.CPP14Parser
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.GoLexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.GoParser
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaLexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaParser
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaScriptLexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.JavaScriptParser
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.KotlinLexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.KotlinParser
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.PythonLexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.PythonParser
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.RustLexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.RustParser
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.TypeScriptLexer
import uk.ac.warwick.dcs.sherlock.module.model.base.lang.TypeScriptParser
import java.io.InputStream

internal enum class ASTDiffRegistry {
    RUST {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree =
            GumTreeConverter.convertFrom(
                inputStream,
                ::RustLexer,
                ::RustParser,
                RustParser::crate
            )
    },

    KOTLIN {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree =
            GumTreeConverter.convertFrom(
                inputStream,
                ::KotlinLexer,
                ::KotlinParser,
                KotlinParser::kotlinFile
            )
    },

    JAVA {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree =
            GumTreeConverter.convertFrom(
                inputStream,
                ::JavaLexer,
                ::JavaParser,
                JavaParser::compilationUnit
            )
    },

    CPP {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree =
            GumTreeConverter.convertFrom(
                inputStream,
                ::CPP14Lexer,
                ::CPP14Parser,
                CPP14Parser::translationUnit
            )
    },

    GO {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree =
            GumTreeConverter.convertFrom(
                inputStream,
                ::GoLexer,
                ::GoParser,
                GoParser::sourceFile
            )
    },

    PYTHON {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree {
            val convertFrom = GumTreeConverter.convertFrom(
                inputStream,
                ::PythonLexer,
                ::PythonParser,
                PythonParser::file_input
            )
            return convertFrom
        }
    },

    JAVASCRIPT {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree {
            val convertFrom = GumTreeConverter.convertFrom(
                inputStream,
                ::JavaScriptLexer,
                ::JavaScriptParser,
                JavaScriptParser::program
            )
            return convertFrom
        }
    },

    TYPESCRIPT {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree {
            val convertFrom = GumTreeConverter.convertFrom(
                inputStream,
                ::TypeScriptLexer,
                ::TypeScriptParser,
                TypeScriptParser::program
            )
            return convertFrom
        }
    },

    UNKNOWN {
        override suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree {
            println("=== WARNING: CONVERTING UNKNOWN FILE TYPE ! ===")
            println("=== please ensure it has correct ANTLR setup ! ===")
            return GumTree()
        }
    };

    protected abstract suspend fun generateGumTreeFrom(inputStream: InputStream): GumTree

    companion object {
        private val extensionToRegistry = mapOf(
            "rs" to RUST,
            "kt" to KOTLIN,
            "java" to JAVA,
            "cpp" to CPP,
            "c" to CPP,
            "h" to CPP,
            "hpp" to CPP,
            "cc" to CPP,
            "cxx" to CPP,
            "hxx" to CPP,
            "hh" to CPP,
            "go" to GO,
            "py" to PYTHON,
            "js" to JAVASCRIPT,
            "cjs" to JAVASCRIPT,
            "mjs" to JAVASCRIPT,
            "ts" to TYPESCRIPT,
            "mts" to TYPESCRIPT,
        )

        suspend fun transformToGumTreeFrom(file: ISourceFile): GumTree {
            val fileExtension = file.fileDisplayName.substringAfterLast('.', file.fileDisplayName)
            return (extensionToRegistry[fileExtension] ?: UNKNOWN).generateGumTreeFrom(file.fileContents)
        }
    }
}