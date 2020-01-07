# JMod with Maven

This example is a bit more complex using the maven jmod plugin.

Setting up the pom file isn't to special, but the jmod plugin is early in alpha stages, so a special repository needs to be added..

    <pluginRepository>
        <id>apache.snapshots</id>
        <name>Apache Development Snapshot Repository</name>
        <url>https://repository.apache.org/content/repositories/snapshots/</url>
        <releases>
            <enabled>false</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>

Then it can be added as a normal plugin, and packaging can be set to "jmod".

Creating the jmods.

    mvn package
    
This will compile the classes and create the respective jmods. Then creating a jlink is quite simple for this example. 
Running the following command from the "target" directory should create a new folder `sp-linked` with the jvm.

    jlink --module-path jmods --add-modules sensorplotting --output sp-linked

The application can be started with.

    ./sp-linked/bin/java org.orangepalantir.SensorPlottingApp

    
## What about the JLink plugin?

The jlink plugin is horrendous. I tried it once, it takes a complicated structure using modules and sub-modules. 
On top of that, it is broken unless you use alpha-2 which means you have to add a repository (same as the jmod plugin).

I found the jmod plugin to work well for creating a jmod, and then running the jlink command 
is simple. Also, from what I gather, the jlink plugin works for compiling modules, 
so it is probably handy to have a module first, then build a jlink project that encapsulates the jmods.