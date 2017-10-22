package Demo
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary
import com.hankcs.hanlp.tokenizer.BasicTokenizer
import scala.io.Source
object nb extends App{
  HanLP.Config.ShowTermNature = false

  var testFile = Array("pTesting.txt","nTesting.txt")
  var trainFile = Array("pTrain2.txt","nTrain.txt")
  var predictCount = Array(0,0)
  var odds = Array(1.0,1.0)

  for (k <- 0 to 1) {
    //k: 0 - P , 1 - N (Test)
    val test = Source.fromFile(testFile(k))
    val testLines = test.getLines()

    var allTest = 0
    predictCount(0) = 0
    predictCount(1) = 0

    for (tLine <- testLines) {
      allTest += 1
      val testData = HanLP.segment(tLine)
      CoreStopWordDictionary.apply(testData)
      print(testData)
      println()

      for (l <- 0 to 1) {
        //l: 0 - P, 1 - N (Train)
        //for each case (sentence)
        var firstscan = true
        var countAll = 0
        odds(l) = 1.0

        //for each word in one case
        for (i <- 0 until testData.size()) {
          if (com.hankcs.hanlp.utility.TextUtility.isChinese(testData.get(i).toString.charAt(0))) {
            var countFound = 1
            val source = Source.fromFile(trainFile(l))
            val trainLines = source.getLines()

            //search the trainFile
            for (line <- trainLines) {
              val trainData = BasicTokenizer.segment(line)
              CoreStopWordDictionary.apply(trainData)

              for (j <- 0 until trainData.size())
                if (com.hankcs.hanlp.utility.TextUtility.isChinese(trainData.get(j).toString.charAt(0))) {
                  if (testData.get(i).toString == trainData.get(j).toString) countFound += 1
                  if (firstscan) countAll += 1
                }
            }
            val word_odds = countFound / (countAll + 2.0)
            odds(l) *= word_odds
            firstscan = false
          }
        }
        println(odds(l))
      }

      val r = odds(0) - odds(1)
      if (r > 0) {
        println("好评")
        predictCount(0) += 1
      }
      else {
        println("差评")
        predictCount(1) += 1
      }
    }
    println(predictCount(k), allTest, predictCount(k) / (allTest + 0.0))
  }
}