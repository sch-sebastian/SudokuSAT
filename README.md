# .sdq file -Syntax

Files used to encode and input a sudoku problem for this program have the ending `.sdq` and most currently be located
in `src\main\data`. An .sdq file can consist of multiple clauses, each one surrounded with`{ }`. Each clause is used to
describe a constraint that the solution should satisfy. Each clause begins with the constraints name followed by `:`
.<br>

Some constraints require to specify cells by their coordinates in the sudoku-grid. The used coordinates have their
origin in the top left corner of the sudoku grid. The X-Axis goes from left to right, and the Y-Axis from top to bottom
which can be seen below:

```
----------------------------------------------
| 11 | 21 | 31 | 41 | 51 | 61 | 71 | 81 | 91 | 
----------------------------------------------
| 12 | 22 | 32 | 42 | 52 | 62 | 72 | 82 | 92 |
----------------------------------------------
| 13 | 23 | 33 | 43 | 53 | 63 | 73 | 83 | 93 |
----------------------------------------------
| 14 | 24 | 34 | 44 | 54 | 64 | 74 | 84 | 94 |
----------------------------------------------
| 15 | 25 | 35 | 45 | 55 | 65 | 75 | 85 | 95 |
----------------------------------------------
| 16 | 26 | 36 | 46 | 56 | 66 | 76 | 86 | 96 |
----------------------------------------------
| 17 | 27 | 37 | 47 | 57 | 67 | 77 | 87 | 97 |
----------------------------------------------
| 18 | 28 | 38 | 48 | 58 | 68 | 78 | 88 | 98 |
----------------------------------------------
| 19 | 29 | 39 | 49 | 59 | 69 | 79 | 89 | 99 |
----------------------------------------------
```

## Constraints:

### {Sudoku:}

To enforce all normal sudoku rules use `{Sudoku:}`<br>
Each row must contain each number from 1 to 9 exactly once.<br>
Each column must contain each number from 1 to 9 exactly once.<br>
Each 3x3 box must contain each number from 1 to 9 exactly once.<br>

### {Sudoku: row}

To enforce only one or multiple specific sudoku rules.<br>
In this example: Each row must contain each number from 1 to 9 exactly once.<br>
The same can be done with `column` and `box`.<br>
Combinations are possible: `{Sudoku: row column}`.<br>
`{Sudoku: row column box}` is equivalent to `{Sudoku:}`.

### {Grid:}

Use the `Grid` constraint as shown below to describe the initial hints (cells that already have an assigne value).<br>
(Currently only 9x9 grids are allowed)

```
{Grid: 
0 0 0 0 0 3 0 8 0
0 0 6 0 0 0 0 0 0
0 1 0 6 0 0 0 5 0
0 0 5 0 4 0 0 0 0
4 8 0 2 0 7 0 0 0
6 0 0 0 0 0 0 7 0
0 0 4 0 2 0 1 6 7
0 0 0 0 5 8 4 0 0
1 0 0 0 0 0 8 0 0
}
```

### {AntiKnight:}

Use `{AntiKnight:}` to state that two cells that are a knight's distance away from each other are not allowed to have
the same value.

### {Killer:}

*Optimization: 0,2,3*<br>
Use the `Killer` constraint as shown below describe the cells that belong to a killer cage and what the cage sums should
be.

```
{Killer:
20
12 31 45 10 21 20 09 11 13 21 28 20 45 11 33 09 21 22 10 10
01 01 02 02 03 04 04 05 05
01 02 02 03 03 06 07 07 05
02 02 03 03 06 06 00 07 08
02 03 03 09 09 06 10 10 08
03 03 11 09 00 12 10 13 13
14 11 11 11 12 12 13 13 15
14 16 00 17 17 13 13 15 15
18 16 16 17 13 13 15 15 19
18 18 20 20 13 15 15 19 19
}
```

The first number `20` states how many cages there are.<br>
The numbers `12 31 45 10 21 20 09 11 13 21 28 20 45 11 33 09 21 22 10 10` states for each cage what it sum should
be.<br>
The grid

```
01 01 02 02 03 04 04 05 05
01 02 02 03 03 06 07 07 05
02 02 03 03 06 06 00 07 08
02 03 03 09 09 06 10 10 08
03 03 11 09 00 12 10 13 13
14 11 11 11 12 12 13 13 15
14 16 00 17 17 13 13 15 15
18 16 16 17 13 13 15 15 19
18 18 20 20 13 15 15 19 19
```

states for each cell to which cage it belongs. Cage numbers start at `01` and must be chosen in the order corresponding
to the previously stated sums.

### {SandwichSum:}

*Optimization: 0,1,2*<br>
Use `SandwichSum` to describe in which rows or columns a SandwichSum must have a certain value.<br>

```
{SandwichSum:
3
7 6
8 10
9 25
2
1 4
5 33
}
```

The `3` states that there must be 3 Column-SandwichSums.<br>
Then `7 6` states that in column 7 the SandwichSum must be 6.<br>
The `2` states that there must be 2 Row-SandwichSums.<br>
Then `1 4` states that in row 1 the SandwichSum must be 4.<br>
(If there are no Row-SandwichSums the later part can be left away, but if there are no Column-SandwichSums this must be
stated.)<br>

### {ArrowHead:}

Use `ArrowHead` to specify that one of two cells must have a smaller value. Always list a pair of two cells, first the
one which should have a smaller value. Cells are specified by their coordinates. The following example states that the
cell with coordinates (x=1,y=1) must have a smaller value then the cells with coordinates (x=2,y=1) and (x=1,y=2):

```
{ArrowHead:
11 21
11 12
}
```

If `EQ` is written before the first pair of cells, the two cells of all pairs are allowed to have equal values.

### {Thermometer:}

Use `Thermometer` to specify the position of thermometers in the Grid. In a thermometer the lowest digit is at the
bulb (the cell listed first) and the values of the following cells are strictly increasing. A `0` marks the beginning of
a new thermometer, then the coordinates of the thermometer's cells follow. The following example specifies two
thermometers, one with its bulb in the cell with coordinates (x=3, y=3) and one with its bulb in the cell with
coordinates(x=1,y=1).

```
{Thermometer:
0 33 32 21
0 11 12
}
```

If `frozen` is written before the first thermometer, cells along the thermometers must only have increasing but not
strictly increasing values.

### {ThermometersHidden:}

Use `ThermometersHidden` to specify the shape of hidden thermometers that must be positioned inside the sudoku grid.
Thermometers are not allowed to overlap and can not be frozen. A `0` marks the beginning of
a new thermometer's shape, then the directions follow (starting from the bulb).
The directions are encoded with:
```  
      1
      
      A
      |
4  <-- -->  2
      |
      V
      
      3
```
In the following Example, a thermometer of length 4 is specified:
```
{ThermometersHidden:
0 1 4 1
}
```
This thermometer has  shape:
```
-----
| I |
---------
| I | I |
---------
    | O |
    -----
```

### {Difficulty:}

Use `Difficulty` to specify a cell that can have multiple values. The different values affect the difficulty of the
sudoku for a human solver. Example:

```
{Difficulty:
55 1 3
}
```

The cell with coordinates (x=5, y=5) can have values in the range from `1` to `3` including `1` and `3`.<br>
Using this constraint will cause multiple runs of the solver, trying all the possible difficulty values in the given
range.

## Reference Solution

It is possible to provide a reference solution that will be compare to the one the SAT-solver finds.<br>
This can be done by adding `{Solution:...` to a file as shown in the example below.

```
{Solution:
2 3 6 9 4 1 8 7 5
9 5 4 3 7 8 6 1 2
8 7 1 6 2 5 4 3 9
1 8 2 4 3 9 7 5 6
3 9 7 8 5 6 1 2 4
6 4 5 2 1 7 3 9 8
4 1 3 5 6 2 9 8 7
5 6 9 7 8 3 2 4 1
7 2 8 1 9 4 5 6 3
}
```

(Care must be taken to check if the solution is unique, otherwise the SAT-solver may find a solution different from the
provided one)

## Level of Optimization

Before the closing `}` of a constraint it is possible to state the optimisation level that should be used for the PBC
with `[x]`. A higher level of optimisation normally means a smaller number of clauses, but this does not have to make
the solver faster.<br>

| x  |     meaning     |
|:----------:|:-------------|
| 0 |  Naive PBCs for sums, all possible combinations of the numbers 1 to 9 get encoded |
| 1 |  Check if the targeted sum can even be achieved with a given number of cells before creating a PBC   |
| 2 |  Only allow weights (values) in a PBC that can potentially be used to achive the targeted sum with a given number of cells  |
| 3 |  Do not use PBCs, but Support clauses with knowledge from the Powerset of {1,...,9}.   |

The different levels are based on each other, so `2` also fulfills `1` (except `3`).<br>
Currently this only has an effect on `SandwichSum` and `Killer`.

## PBC-Encoding

It is possible to select a type of PBC-Translation by adding `{PBC:x}` to a .sdq file.

| x  |     meaning     |
|:----------:|:-------------|
| BDD |  PBC are translated to CNF using a binary-decision-diagram |
| AdderNetwork |  PBC are translated to CNF using an adder-network  |

The translation type will then be used for all PBC constraints during the encoding.<br>
AdderNetworks are used if not stated differently, they normally create fewer clauses up to a degree that SAT solver
finishes significantly faster. 

