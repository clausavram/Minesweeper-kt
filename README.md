# Minesweeper.kt
This is a solution to the [Minesweeper project](https://hyperskill.org/projects/8) on JetBrains Academy at hyperskill.org

### How to run
Windows:
```sh
> .\gradlew.bat --console=plain --quiet run
```
Linux:
```sh
$ ./gradlew --console=plain --quiet run
```
Example output:
```
PS E:\Programming\IntelliJ\Minesweeper.kt> .\gradlew.bat --console=plain --quiet run
How many mines do you want on the field? 4

 |123456789|
-|---------|
1|.........|
2|.........|
3|.........|
4|.........|
5|.........|
6|.........|
7|.........|
8|.........|
9|.........|
-|---------|
Set/delete mines marks (x and y coordinates): 5 5 free

 |123456789|
-|---------|
1|.........|
2|.........|
3|.........|
4|.........|
5|....2....|
6|.........|
7|.........|
8|.........|
9|.........|
-|---------|

...

Set/delete mines marks (x and y coordinates): 3 2 mine

 |123456789|
-|---------|
1|/1.1/////|
2|/1*1/////|
3|12221////|
6|//111////|
7|/////////|
8|/////////|
9|/////////|
-|---------|
Congratulations! You found all mines!
```

Running the console app using a square font (char width = height) is really helpful.\
Example font: [Square](http://strlen.com/square/)