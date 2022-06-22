import kotlin.system.exitProcess

class Player(val name: String) {
    var score = 0
}

class ConnectFour {
    var rows = 6
    var cols = 7
    val dimensionTemplate = "\\d+([xX])\\d+".toRegex()
    val firstPlayer: Player
    val secondPlayer: Player
    val gameBoard: MutableList<MutableList<Char>>
    var currentTurn = true // true if first player
    var disc = 'o'
    var currentPlayerName: String
    var whoMovedFirst = true // true if first player
    val numberOfGames: Int
    val numberOfGamesMessage: String
    var currentGameNumber = 1
    var gamesLeft: Int
    val pointsForWin = 2

    init {
        println("Connect Four")
        println("First player's name:")
        firstPlayer = Player(readLine()!!)
        println("Second player's name:")
        secondPlayer = Player(readLine()!!)
        askDimension()
        numberOfGames = askSingleOrMultipleGames()
        gamesLeft = numberOfGames
        numberOfGamesMessage = if (numberOfGames == 1) "Single game" else "Total $numberOfGames games"
        gameBoard = MutableList(rows) { MutableList(cols) { ' ' } }
        currentPlayerName = firstPlayer.name
        startMessage()
        startGame()
    }

    fun askDimension() {
        while(true) {
            println("Set the board dimensions (Rows x Columns)\n" +
                    "Press Enter for default (6 x 7)")
            val dimension = readLine()!!.lowercase().replace("\\s+".toRegex(), "")
            when {
                dimension.isEmpty() -> return
                !dimensionTemplate.matches(dimension) -> println("Invalid input")
                else -> {
                    rows = dimension.substringBefore('x').toInt()
                    cols = dimension.substringAfter('x').toInt()
                    if (rows !in 5..9) {
                        println("Board rows should be from 5 to 9")
                    } else if (cols !in 5..9) {
                        println("Board columns should be from 5 to 9")
                    } else {
                        return
                    }
                }
            }
        }
    }

    fun askSingleOrMultipleGames(): Int {
        while(true) {
            println(
                "Do you want to play single or multiple games?\n" +
                        "For a single game, input 1 or press Enter\n" +
                        "Input a number of games:"
            )
            val playerInput = readLine()!!
            val numberOfGames = playerInput.toIntOrNull()
            if (playerInput.isEmpty()) {
                return 1
            } else if (numberOfGames == null ||
                numberOfGames <= 0
            ) {
                println("Invalid input")
            } else {
                return numberOfGames
            }
        }
    }

    fun startMessage() {
        println("${firstPlayer.name} VS ${secondPlayer.name}\n" +
                "$rows X $cols board\n" +
                numberOfGamesMessage
        )
    }

    fun startGame() {
        if (numberOfGames > 1) println("Game #$currentGameNumber")
        showBoard()
        while(!isBoardFull()) {
            makeMove()
            showBoard()
            checkIfWin(disc, cols)
            changeTurn()
        }
    }

    fun makeMove() {
        val col = getPlayerCol()
        for (i in 1 until rows) {
            if (gameBoard[i][col] != ' ') {
                gameBoard[i - 1][col] = disc
                return
            }
        }
        gameBoard[rows - 1][col] = disc // if column is empty
    }

    fun getPlayerCol(): Int {
        while(true) {
            println("$currentPlayerName's turn:")
            val playerInput = readLine()!!
            val playerCol = playerInput.toIntOrNull()
            if (playerInput == "end") {
                gameOver()
            } else if (playerCol == null) {
                println("Incorrect column number")
            } else if (playerCol !in 1..cols) {
                println("The column number is out of range (1 - $cols)")
            } else if (isColumnFull(playerCol - 1)) {
                println("Column $playerCol is full")
            } else {
                return playerCol - 1
            }
        }
    }

    fun isColumnFull(col: Int): Boolean {
        return gameBoard[0][col] != ' '
    }

    fun checkIfWin(disc: Char, cols: Int): Boolean {
        val delimiterToHorizontalWin = ""
        val delimiterToVerticalWin = ".{$cols}"
        val delimiterToLeftDiagonalWin = ".{${cols + 1}}"
        val delimiterToRightDiagonalWin = ".{${cols - 1}}"

        val listOfDelimiters = listOf(
            delimiterToHorizontalWin,
            delimiterToVerticalWin,
            delimiterToLeftDiagonalWin,
            delimiterToRightDiagonalWin
        )
        val validRegexDisc = if (disc == '*') "\\*" else "o" // '*' is a special regex character, so we need to escape it
        val matches = listOfDelimiters.count {
            val winningRegex = Regex("$validRegexDisc$it$validRegexDisc$it$validRegexDisc$it$validRegexDisc")
            gameBoardToOneRowString().contains(winningRegex)
        }
        if (matches != 0) {
            println("Player $currentPlayerName won")
            if (currentPlayerName == firstPlayer.name) {
                firstPlayer.score += pointsForWin
            } else {
                secondPlayer.score += pointsForWin
            }
            printScore()
            nextGameOrGameOver()
        }
        return false
    }

    fun gameBoardToOneRowString(): String = gameBoard.joinToString("-") { it.joinToString("") }

    fun isBoardFull(): Boolean {
        if (gameBoard[0].none { it == ' ' }) {
            println("It is a draw")
            firstPlayer.score++ ; secondPlayer.score++
            printScore()
            nextGameOrGameOver()
        }
        return false
    }

    fun changeTurn() {
        if (currentTurn) {
            currentPlayerName = secondPlayer.name
            disc = '*'
        } else {
            currentPlayerName = firstPlayer.name
            disc = 'o'
        }
        currentTurn = !currentTurn
    }

    fun showBoard() {
        println()
        for (j in 1..cols) print(" $j")
        println()
        for (row in gameBoard) {
            println(row.joinToString("║", "║", "║"))
        }
        println("╚${"═╩".repeat(cols - 1)}═╝")
    }

    fun nextGameOrGameOver() {
        gamesLeft--
        if (gamesLeft > 0) {
            currentGameNumber++
            if (currentTurn == whoMovedFirst) changeTurn()
            whoMovedFirst = !whoMovedFirst
            cleanBoard()
            startGame()
        } else {
            gameOver()
        }
    }

    fun printScore() {
        println("\nScore\n${firstPlayer.name}: ${firstPlayer.score} ${secondPlayer.name}: ${secondPlayer.score}")
    }

    fun cleanBoard() {
        for (row in gameBoard) {
            row.replaceAll { ' ' }
        }
    }

    fun gameOver() {
        println("Game over!")
        exitProcess(0)
    }
}

fun main() {
    val game = ConnectFour()
}