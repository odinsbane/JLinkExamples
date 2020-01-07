# Compiling, Building and Running

First compile the source code.

    javac -d target/build module-info.java hellopackage/HelloWorld.java
    
Change to the target folder and jlink it.

    jlink --module-path build --add-modules hellomodule --output helloworld
    
Then the program can be run using the custom jvm.

    ./helloworld/bin/java hellopackage.HelloWorld
    
I made all of the names different so the different elements can be distinguished. 

    module hellomodule{
        exports hellopackage;
    }

We have a package that is exported as part of our module.

## TODO

Have a script/entrypoint output so that we don't need to type out `java packagename.MainClass`.

