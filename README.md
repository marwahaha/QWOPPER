# QWOPPER: a programmatic interface for [QWOP](http://www.foddy.net/Athletics.html)

The intended goal of the project is to write a bot able to play [QWOP](http://www.foddy.net/Athletics.html).

## Evolution
### 2008, [Bennett Foddy](http://foddy.net/)
Bennett Foddy created [the popular computer game](http://www.foddy.net/Athletics.html) in 2008.
Since then, millions of people have played!

[The Wikipedia entry has more background on the game.](https://en.wikipedia.org/wiki/QWOP)


### 2011, [Laurent Vaucher](https://slowfrog.blogspot.com/)
Laurent Vaucher built the first version of ["QWOPPER"](https://github.com/slowfrog/qwopper).
He tried using genetic algorithms to learn to play the game.

That work is detailed in [their blog post](http://slowfrog.blogspot.com/2011/03/genetically-engineered-qwop-part-1.html).

### 2013, [Steven Ray](https://github.com/pizzapotamus)
Steven Ray developed an improved version of ["QWOPPER"](https://github.com/pizzapotamus/Qwopper) while a Master's student at [Sac State](https://www.csus.edu/).
He along with his advisor [V. Scott Gordon](https://athena.ecs.csus.edu/~gordonvs/) and Laurent Vaucher successfully submitted a paper titled ["Evolving QWOP Gaits"](https://ai.google/research/pubs/pub42902) to [Genetic and Evolutionary Computation Conference 2014](http://www.sigevo.org/gecco-2014/).

See the [Youtube video here](https://www.youtube.com/watch?v=eWxFI3NHtT8) or [download the paper here](http://athena.ecs.csus.edu/~gordonvs/papers/QWOPgecco14.pdf). The DOI is [10.1145/2576768.2598248](http://doi.org/10.1145/2576768.2598248).

### 2019, [Kunal Marwaha](http://kunalmarwaha.com/)
I'm trying to make this application work with modern tooling. I'm also trying to get a good walking gait in QWOP.

[My code is here](https://github.com/marwahaha/QWOPPER).

## Using
1. Clone project: `git clone https://github.com/marwahaha/QWOPPER.git` (or [download the ZIP](https://github.com/marwahaha/QWOPPER/archive/master.zip))
2. Navigate into the directory: `cd QWOPPER`
3. Launch the graphical interface: `./gradlew run` (`gradlew.bat run` on Windows).
4. Resize the graphical interface to half-screen, and open [QWOP](http://www.foddy.net/Athletics.html) in the other half screen.

## Contributing tips
* You'll need to [install Gradle](https://gradle.org/install/).
* You can run the JAR directly with `java -jar build/libs/QWOPPER.jar`.
* Lots of [Gradle tips here](https://github.com/shekhargulati/gradle-tips).

More details coming soon!
