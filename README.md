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
I've cleaned up and updated this application for modern (circa-2019) tooling.
With this program, I'm aiming to get a good walking gait in QWOP.
I may or may not use genetic algorithms, as I have some idea of what "walking" looks like.

[My code is here](https://github.com/marwahaha/QWOPPER).

## Using
1. Clone project: `git clone https://github.com/marwahaha/QWOPPER.git` (or [download the ZIP](https://github.com/marwahaha/QWOPPER/archive/master.zip))
2. Navigate into the directory: `cd QWOPPER`
3. Launch the graphical interface: `./gradlew run` (`gradlew.bat run` on Windows).
4. Resize the graphical interface to half-screen, and open [QWOP](http://www.foddy.net/Athletics.html) in the other half screen.
5. Click *Find game area*. Enter a Gait "DNA" or press *Random*. Press *Run* and watch!

## Gait "DNA"
The gait "DNA" is a string of letters A-P. Each letter encodes which of `[Q, W, O, P]` are pressed, each held for 150ms. Each of 2\*2\*2\*2=16 options are listed below.

When the gait string is complete, the computer starts again from the first letter. This continues until the runner crashes, the game is complete (100 meters), or the program is stopped.

| Letter | Input |
| ---- | ----- |
| A | Q |
| B | W |
| C | O |
| D | P |
| E | QW |
| F | QO |
| G | QP |
| H | WO |
| I | WP |
| J | OP |
| K | QWO |
| L | QWP |
| M | QOP |
| N | WOP |
| O | QWOP |
| P | (none pressed) |

## Contributing tips
* You'll need to [install Gradle](https://gradle.org/install/).
* You can run the JAR directly with `java -jar build/libs/QWOPPER.jar`.
* Lots of [Gradle tips here](https://github.com/shekhargulati/gradle-tips).

## Acknowledgements
* To Bennett Foddy for creating the game and thoughts on [physics in game design](https://www.youtube.com/watch?v=NwPIoVW65pE).
* To Laurent Vaucher and Steven Ray for [previous work](https://storage.googleapis.com/pub-tools-public-publication-data/pdf/42902.pdf), and allowing their code to be MIT-licensed.
* To Henry Chang for testing the software and discussions on gradient descent, genetic algorithms, and image processing.
* To Allan Peng and Madison Ashley for discussions about programmatically playing QWOP.

Thanks also to:
* Discussions ([here](https://slowfrog.blogspot.com/2011/03/genetically-engineered-qwop-part-1.html#comments), [here](https://www.speedrun.com/QWOP/forum), [here](https://www.wikihow.com/Play-Qwop)) and videos ([here](https://web.archive.org/web/20130417203240/http://challengers.guinnessworldrecords.com/challenges/160-fastest-100m-run-qwop-flash-game), [here](https://www.youtube.com/watch?v=GRYHtI__lJg), [here](https://www.youtube.com/watch?v=YbYOsE7JyXs), [here](https://www.youtube.com/watch?v=hRRURUjqmG0), [here](https://www.youtube.com/watch?v=uts1GuVpvfM), [here](https://www.youtube.com/watch?v=HBFYJvq_o_4)) about running in QWOP.
* Other projects ([here](http://whsieh.github.io/qwop-ai/), [here](https://github.com/unixpickle/qwop-ai), [here](http://cs229.stanford.edu/proj2012/BrodmanVoldstad-QWOPLearning.pdf), [here](https://github.com/bpgeck/QwopStyle), [here](https://www.youtube.com/watch?v=e27TUmMkOA0)) about computers learning to play QWOP.
* Projects ([here](http://osim-rl.stanford.edu/), [here](https://github.com/Eelis/GrappleMap/blob/master/README.md), [here](https://github.com/CMU-Perceptual-Computing-Lab/openpose)) about computers detecting and manipulating humanoid shapes.
* Articles ([here](12341234123412341234123412341234244), [here](https://towardsdatascience.com/introduction-to-genetic-algorithms-including-example-code-e396e98d8bf3), [here](http://www.ai-junkie.com/ga/intro/gat1.html)) about gradient descent and genetic algorithms.
* Documentation about [Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance).
* (humorous) The [Ministry of Silly Walks](https://www.youtube.com/watch?v=iV2ViNJFZC8)!
