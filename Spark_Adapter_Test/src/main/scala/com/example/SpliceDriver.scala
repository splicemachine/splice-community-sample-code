package com.example


import com.splicemachine.derby.impl.SpliceSpark
import com.splicemachine.spark.splicemachine.SplicemachineContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SpliceDriver {

  def main(args: Array[String]) {

    val conf = new SparkConf()
    conf.set("spark.serializer", "com.splicemachine.serializer.SpliceKryoSerializer")
    conf.set("spark.kryo.registrator", "com.splicemachine.derby.impl.SpliceSparkKryoRegistrator")
    val spark = SparkSession.builder().appName("Reader").config(conf).getOrCreate()
    SpliceSpark.setContext(spark.sparkContext)
    val dbUrl = "jdbc:splice://stl-colo-srv137:1527/splicedb;user=splice;password=admin"
    val splicemachineContext = new SplicemachineContext(dbUrl)
    splicemachineContext.df("SELECT * FROM sys.systables").show()


  }
}