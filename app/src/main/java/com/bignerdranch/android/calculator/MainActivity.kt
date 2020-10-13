package com.bignerdranch.android.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import java.util.Queue
import java.util.LinkedList

private val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    //Buttons
    private lateinit var calculatorScreen : TextView
    private lateinit var n0 : Button
    private lateinit var n1 : Button
    private lateinit var n2 : Button
    private lateinit var n3 : Button
    private lateinit var n4 : Button
    private lateinit var n5 : Button
    private lateinit var n6 : Button
    private lateinit var n7 : Button
    private lateinit var n8 : Button
    private lateinit var n9 : Button
    private lateinit var decimal : Button
    private lateinit var divide : Button
    private lateinit var multiply : Button
    private lateinit var subtract : Button
    private lateinit var addition : Button
    private lateinit var equal : Button
    private lateinit var clear : Button
    private lateinit var sign_change : Button
    private lateinit var backspace : Button

    //the entire calculationString will be evaluated on
    private var calculationString : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Define all the buttons from the calculator
        calculatorScreen = findViewById(R.id.CalculatorScreen)
        n0 = findViewById(R.id.n0)
        n1 = findViewById(R.id.n1)
        n2 = findViewById(R.id.n2)
        n3 = findViewById(R.id.n3)
        n4 = findViewById(R.id.n4)
        n5 = findViewById(R.id.n5)
        n6 = findViewById(R.id.n6)
        n7 = findViewById(R.id.n7)
        n8 = findViewById(R.id.n8)
        n9 = findViewById(R.id.n9)
        decimal = findViewById(R.id.dot)
        divide = findViewById(R.id.divide)
        multiply = findViewById(R.id.multiply)
        subtract = findViewById(R.id.subtract)
        addition = findViewById(R.id.addition)
        equal = findViewById(R.id.equals)
        clear = findViewById(R.id.clear)
        sign_change = findViewById(R.id.sign_change)
        backspace = findViewById(R.id.backspace)


        //click listener for all the buttons
        sign_change.setOnClickListener{
            //do nothing, no functionality added currently
        }
        decimal.setOnClickListener{

            addToScreen(addDecimal())
        }
        n0.setOnClickListener{

            addToScreen(addDigit("0"))
        }
        n1.setOnClickListener{

            addToScreen(addDigit("1"))
        }
        n2.setOnClickListener{

            addToScreen(addDigit("2"))
        }
        n3.setOnClickListener{

            addToScreen(addDigit("3"))
        }
        n4.setOnClickListener{

            addToScreen(addDigit("4"))
        }
        n5.setOnClickListener{

            addToScreen(addDigit("5"))
        }
        n6.setOnClickListener{

            addToScreen(addDigit("6"))
        }
        n7.setOnClickListener {

            addToScreen(addDigit("7"))
        }
        n8.setOnClickListener{

            addToScreen(addDigit("8"))
        }
        n9.setOnClickListener{

            addToScreen(addDigit("9"))
        }
        divide.setOnClickListener{

            if(operationIsValid()) {
                addToScreen("/")
            }
        }
        multiply.setOnClickListener {

            if(operationIsValid()) {
                addToScreen("*")
            }
        }
        subtract.setOnClickListener{

            if(operationIsValid()) {
                addToScreen("-")
            }
        }
        addition.setOnClickListener{

            if(operationIsValid()) {
                addToScreen("+")
            }
        }
        clear.setOnClickListener {

            calculationString = ""
            calculatorScreen.text = calculationString
        }
        backspace.setOnClickListener{

            if(calculatorScreen.text == "ERROR"){//if outcome is error, clear
                calculationString = ""
            }
            else if (calculationString.isNotEmpty()) {//if not error and not empty, remove the last char
                calculationString = calculationString.substring(0,calculationString.length-1)
            }
            calculatorScreen.text = calculationString
        }
        equal.setOnClickListener{
            //computes the entire equation in O(n) time
            //gives error if not correct format
            equationOutcome()
        }
    }

    private fun addDigit(number: String) : String {
        /* Max # of significant digits is 15 total
         * whole #'s (max=14) and decimal #'s (max=16)
         */
        val operations : Set<Char> = setOf('/', '*', '-', '+')
        var lastOperatorIndex : Int = -1
        var index = 0
        var tempString : String = ""
        //append the number sequence after operator(if any)
        calculationString.forEach { c ->
            if(operations.contains(c)){
                lastOperatorIndex = index
                //new operator found, reset string
                tempString = ""
            }
            if(lastOperatorIndex < index){
                tempString += calculationString[index]
            }
            index++
        }

        //(last number on screen) + (number trying to be added)
        tempString += number

        //must match regular expression
        val regex = Regex("([1-9][0-9]{0,13}|0)(.(([0-9]{0,15}[1-9])|0))?")
        return if(regex.matches(tempString)) number else ""
    }

    private fun equationOutcome() {
        try{
            //used when pressing the "=" operator
            val operations : Set<Char> = setOf('/', '*', '-', '+')
            var opList = mutableListOf<Char>()
            var numberList = mutableListOf<Double>()
            var tempString : String = ""
            var index = 0
            //iterate through the characters of calculation
            calculationString.forEach { c ->
                if(operations.contains(c)){//if operator found
                    opList.add(c)

                    //add 'number' to the list
                    numberList.add(tempString.toDouble())

                    //reset the string for re-evalution
                    tempString = ""
                }
                else{//it's part of a number
                    tempString += c
                }
                index++
            }
            //add the last number string
            numberList.add(tempString.toDouble())

            //check that whole expression follows order
            val expressionRegex = Regex("(([1-9][0-9]{0,13}|0)(.(([0-9]{0,15}[1-9])|0))?)([(*)(+)(-)/](([1-9][0-9]{0,13}|0)(.(([0-9]{0,15}[1-9])|0))?))*")
            if(expressionRegex.matches(calculationString)){
                //proceed: use the conversion algorithm

                var opLowPrecedence = mutableListOf<Char>()
                var lowOpIndex : Int = 0 //operators with low precedence
                index = 0 //reuse the index from before
                var numListModifications : Int = 0
                var calculationIsNotDone = true
                while(calculationIsNotDone){
                    if(index < opList.size){
                        //use: opList(Char), numberList(Double)
                        var currentOp : Char = opList[index]
                        var newNumber : Double
                        when(currentOp){
                            '*' -> {//multiply
                                //operator is used for number at index & index+1
                                newNumber = numberList[index-numListModifications] * numberList[index-numListModifications +1]
                                numberList.removeAt(index-numListModifications +1)
                                numberList[index-numListModifications] = newNumber
                                numListModifications++
                            }
                            '/' -> {//divide
                                //operator is used for number at index & index+1
                                newNumber = numberList[index-numListModifications] / numberList[index-numListModifications +1]
                                numberList.removeAt(index-numListModifications +1)
                                numberList[index-numListModifications] = newNumber
                                numListModifications++
                            }
                            else -> opLowPrecedence.add(currentOp)//add or subtract
                        }
                        index++
                        if(index == opList.size)//reset the modification count to be used later on
                            numListModifications = 0
                    }
                    else{
                        //this means we took care of the multiply and divide operators and reached end of opList
                        if(lowOpIndex < opLowPrecedence.size){
                            //we have more work to do
                            //lowOpIndex(Int), opLowPrecedence(List<Char>), numberList(List<Double>)
                            var newNumber : Double
                            when(opLowPrecedence[lowOpIndex]){
                                '+' -> {
                                    newNumber = numberList[lowOpIndex-numListModifications] + numberList[lowOpIndex-numListModifications +1]
                                    numberList[lowOpIndex-numListModifications] = newNumber//modify the first number
                                }
                                '-' -> {
                                    newNumber = numberList[lowOpIndex-numListModifications] - numberList[lowOpIndex-numListModifications +1]
                                    numberList[lowOpIndex-numListModifications] = newNumber//modify the first number
                                }
                                else -> throw Exception("IllegalOperator: not a valid operator \'${opLowPrecedence[lowOpIndex]}\'")
                            }
                            //common code between + AND -
                            numberList.removeAt(lowOpIndex-numListModifications +1)//delete the second number
                            numListModifications++
                            //now go to next operator
                            lowOpIndex++
                        }
                        else{
                            //we are now done computing everything!!!!
                            //this will exit the entire loop
                            calculationIsNotDone = false
                        }
                    }//end if(index < opList.size)
                }//end while

                //final answer (numberList.size == 1)
                calculationString = numberList[0].toString()//continue computing
                calculatorScreen.text = numberList[0].toString()//view answer
            }
            else{
                //ERROR: does not match the regular expression
                //       reset and print to client
                Log.i(TAG, "Error calculation expression")
                calculationString = ""
                calculatorScreen.text = "ERROR"
            }
        } catch(e: Exception){
            //Possible errors:
            //      casting string to double
            //      division error (divide by zero)
            // -> reset and print to client

            Log.e(TAG, "Equation computation error: ", e)
            calculationString = ""
            calculatorScreen.text = "ERROR"
        }
    }

    private fun addDecimal() : String {
        /*Determines whether to add decimal or not.
         *Checks decimal validity
         */
        val operations : Set<Char> = setOf('/', '*', '-', '+')
        var lastOperatorIndex : Int = -1
        var numberPresent = false
        var index = 0
        calculationString.forEach { c ->
            if(operations.contains(c)){
                lastOperatorIndex = index
                numberPresent = false
            }
            else{
                numberPresent = true
            }
            index++
        }

        //empty string OR operator is the last thing in the calculation
        if(lastOperatorIndex == (calculationString.length-1) && !numberPresent)
            return "0."

        //from (0 OR lastOperatorIndex+1) to (last string index)
        var noDecimalPresent : Boolean = false
        //max number of whole numbers
        val regex : Regex = Regex("([1-9][0-9]{0,13})|0")
        if(regex.matches(calculationString.substring(lastOperatorIndex+1)))
            noDecimalPresent = true //if matches, no decimal present

        return if(noDecimalPresent) "." else ""
    }

    private fun operationIsValid() : Boolean{
        //return false if length = 0, true if last char is Digit
        return when(val length : Int = calculationString.length){
            0 -> {
                false
            }
            else -> {
                val c : Char = calculationString[length-1]
                c.isDigit()
            }
        }
    }

    private fun addToScreen(n : String){
        calculationString += n
        calculatorScreen.text = calculationString
    }
}