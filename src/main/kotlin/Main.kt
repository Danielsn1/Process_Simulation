import java.util.*
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

//Creates the Bar for each process
fun createBar(time: Process) {
    print("Process: ${time.processNumber}: ")
    var timeInLine: Double = time.timeSpentInLine
    if (timeInLine <= 0.0) print("/")
    while (timeInLine > 0.0) {
        print("*")
        timeInLine -= 0.3
    }
    print("\n")
}

//Creates a bar graph in the terminal of how long a process waited
fun createGraph(times: MutableList<Process>) {
    times.map { createBar(it) }
}

//Creates a class names Process
data class Process(val timeToDoTask: Double, val timeBetween: Double, val processNumber: Int) {
    //initializes variables
    var timeSpentInLine: Double = 0.0
    var arrived: Double = timeBetween
    var beginExecution: Double = timeBetween
    var endExecution: Double = 0.0
}

//creates a function that takes the specified mean and generates numbers closer to zero
fun randomGenerator(): (Double) -> Double {
    return { specifiedMean: Double -> -specifiedMean * ln(Math.random()) }
}

//Creates an instance of Process with randomly generated numbers.
fun createRandomProcess(processNum: Int, taskMean: Double, intervalMean: Double): Process {
    //returns the process using the randomGenerator Function, while also assigning each process an id
    return Process(randomGenerator()(taskMean), randomGenerator()(intervalMean), processNum)
}
//extension function that calculates sum of a list of Processes
fun MutableList<Process>.sum():Double{
    return this.sumOf { it.timeSpentInLine }
}
//extension function that calculates mean of a list of Processes
fun MutableList<Process>.mean():Double{
    return this.sum()/this.size
}
//extension function that calculates standard deviation of a list of Processes
fun MutableList<Process>.std():Double{
    return sqrt(this.sumOf { (it.timeSpentInLine-this.mean()).pow(2) }/this.size)
}
//extension function that calculates the median of a list of Processes
fun MutableList<Process>.median():Double{
    // creates a sorted list of doubles representing the time spent in line
    val sorted = this.map{it.timeSpentInLine}.sortedBy { it }
    return if (sorted.isNotEmpty()){
        if (sorted.size % 2 == 0){
            (sorted[sorted.size/2] + sorted[(sorted.size/2)-1])/2
        } else{
            sorted[sorted.size/2]
        }
        //returns 0 if the given list is empty
    }else 0.0
}

// creates a class that contains and interacts with a queue of Processes
class ProcessQueue(size: Int) {
    //initializes the queue
    private val inProgress: Queue<Process> = LinkedList(listOf())

    //fills the queue with the determined number of Processes, while defining the mean for the time it takes to
    // complete a tasks, and the mean for the time between processes
    init {
        for (i in 0 until size) inProgress.add(createRandomProcess(processNum = i, taskMean = 4.5, intervalMean = 5.0))
    }

    //a wrapper for the queue poll function
    fun pop(): Process {
        return inProgress.poll()
    }

    //a wrapper for the queue peek function
    fun peek(): Process {
        return inProgress.peek()
    }

    //a wrapper for the queue isEmpty function
    fun isEmpty(): Boolean {
        return inProgress.isEmpty()
    }

}


fun main() {


    //creates a list of the completed Processes
    val completed: MutableList<Process> = listOf<Process>().toMutableList()
    var p1:Process
    var p2:Process
    //creates an instance of ProcessQueue
    val queue = ProcessQueue(100)
    //runs through the queue until the queue is empty
    while (!queue.isEmpty()) {
        // get the first two Processes in the queue
        p1 = queue.pop()
        //checks to make sure the queue is still full after the pop

        //calculates when the process ends its execution
        p1.endExecution = p1.beginExecution + p1.timeToDoTask
        //calculates how long the process spent in the queue
        p1.timeSpentInLine = p1.beginExecution - p1.arrived
        completed.add(p1)
        //checks if the queue is empty after popping off the element
        if (!queue.isEmpty()) {
            p2 = queue.peek()
        }else break

        //calculates when a process arrived
        p2.arrived = p1.arrived + p2.timeBetween
        //calculates when a process begins executing
        if (p2.arrived > p1.endExecution) {
            p2.beginExecution = p2.arrived
        } else p2.beginExecution = p1.endExecution
    }

    //prints the Max queue time, Min Queue time, and average Queue time
    println("Maximum time spent in line: ${completed.maxOf { it.timeSpentInLine }}")
    println("Minimum time spent in line: ${completed.minOf { it.timeSpentInLine }}")
    //prints how long the queue took to process
    println("Total time of the queue: ${completed.maxOf { it.endExecution }}")
    //calculates the mean time spent in the queue
    println("The average time spent in line: ${completed.mean()}")
    println("The standard deviation of time spent in line: ${completed.std()}")
    println("The median time spent in line: ${completed.median()}")
    println("Graph of wait time(* = 0.3 seconds or less)('/' Means no wait time)")
    createGraph(completed)
}