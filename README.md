# QWOPPER: programmatic interface for [QWOP](http://www.foddy.net/Athletics.html)

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
So far, I'm just trying to make it work :-)

[Code is here](https://github.com/marwahaha/qwopper-1).

## Usage
1. Install Apache Ant.
* mac: `brew install ant`
* windows: [follow instructions here](https://www.mkyong.com/ant/how-to-install-apache-ant-on-windows/)
* fallback: [see the manual](https://ant.apache.org/manual/install.html)

2. Clone project.
```
git clone https://github.com/marwahaha/Qwopper-1.git qwopper-pizza
cd qwopper-pizza
```

3. Make the JAR file by running `ant package` (or simply `ant`).

4. Run the JAR, which opens a graphical interface:
```
java -jar dist/qwopper.jar
```

5. Resize the graphical interface to half-screen, and open [QWOP](http://www.foddy.net/Athletics.html) in the other half screen.

### Hints
* See all ant tasks with `ant -p`

More details coming soon!
