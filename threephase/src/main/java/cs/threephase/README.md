TPR-4x4x4-Solver
================

4x4x4 Solver = Three-Phase-Reduction Solver + 3x3x3 Solver

# Usage
 - sh make.sh   # generate threephase.jar
 - java -cp .:threephase.jar:twophase.jar test N   # solving N random-state scrambled cubes.

Solve a specific cube from the command line
 - java -cp .:threephase.jar:twophase.jar solver UUURUUUFUUUFUUUFRRRBRRRBRRRBRRRBRRRDFFFDFFFDFFFDDDDBDDDBDDDBDDDLFFFFLLLLLLLLLLLLULLLUBBBUBBBUBBB

# Performance
 - Command: "java -cp .:threephase.jar:twophase.jar test 2000"
 - GUI demo: "java -cp .:threephase.jar:twophase.jar ui.MainProgram"
 - Processor: Intel Core i7 2670QM
 - Average solution length: 44.39 moves(face turn metric).
 - Average solving time: 250 ms.
 - Memory: <= 30M with min2phase package.
 - Initialization time: 6s~7s
 - Solution Length Distribution:
 - 40  5
 - 41  32
 - 42  99
 - 43  275
 - 44  590
 - 45  658
 - 46  309
 - 47  32

# Note
 - At its first executing, about 20M's tables will be generated and written to disk (md5(threephase.data) = 0ee2f10d29af8ada724a1426275512c5).

# Algorithm
 - [Tsai's 8-step 4x4x4 algorithm](http://cubezzz.dyndns.org/drupal/?q=node/view/73#comment-2588)
 - The solver merges Tsai's step3 and step4 into one step and use [min2phase package](https://github.com/ChenShuang/min2phase) to replace Tsai's step5-8

# TODO
 - Speeding up initialization.
 - Solution format.
 - Many bugs which might cause ArrayIndexOutOfBoundsException.

# License GPLv3

    Copyright (C) 2014  Shuang Chen

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
