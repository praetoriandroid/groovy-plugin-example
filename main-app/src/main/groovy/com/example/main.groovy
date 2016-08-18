package com.example

import com.example.plugin.Plugin

import java.util.jar.JarFile

def launcher = new File(launcherPath).name
plugins.findAll { plugin ->
    plugin != launcher
}.each { plugin ->
    def pluginFile = new File("$launcherDir/$plugin")
    println "Processing plugin file $pluginFile.name"

    URL[] urls = [new URL("jar:file:$pluginFile.absolutePath!/")]
    URLClassLoader classLoader = URLClassLoader.newInstance(urls)

    Plugin pluginEntryPoint
    new JarFile(pluginFile).entries().each { entry ->
        if (!entry.isDirectory() && entry.name.endsWith(".class")) {
            String className = entry.name[0..-7]
            className = className.replace('/', '.')
            println "  found class entry: $entry.name -> $className"
            Class pluginClass = classLoader.loadClass(className)
            pluginClass.getInterfaces().find { interfaceClass ->
                if (interfaceClass == Plugin) {
                    println "  found plugin entry point: $pluginClass.name"
                    Class<? extends Plugin> pluginEntryPointClass = pluginClass
                    pluginEntryPoint = pluginEntryPointClass.newInstance()
                }
            }
        }
    }

    println()
    if (pluginEntryPoint) {
        println "  plugin '$pluginEntryPoint.title': the answer is '$pluginEntryPoint.theAnswer'"
    } else {
        println "  broken plugin: entry point was not found"
    }
}

static List<String> getPlugins() {
    getLauncherDir().list().findAll { file ->
        file.endsWith('.jar')
    }
}

static File getLauncherDir() {
    def runningExecutable = getLauncherPath()
    def launcherDir = new File(runningExecutable).parentFile
    assert launcherDir
    launcherDir
}

private static String getLauncherPath() {
    main.class.protectionDomain.codeSource.location.toURI().path
}

