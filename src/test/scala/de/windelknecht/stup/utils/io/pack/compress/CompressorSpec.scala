//package de.windelknecht.stup.utils.io.pack.compress
//
//import akka.actor.ActorSystem
//import akka.testkit.{DefaultTimeout, ImplicitSender, TestKit}
//import de.windelknecht.stup.utils.io.pack.compressor.CompressError
//import de.windelknecht.stup.utils.security.ChecksumTools
//import de.windelknecht.stup.utils.tools.testing.VFSHelper
//import org.apache.commons.compress.compressors.CompressorStreamFactory
//import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
//import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
//import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
//
//import scala.concurrent.duration._
//import scala.language.postfixOps
//
///**
// * Created by Me.
// * User: Heiko Blobner
// * Mail: heiko.blobner@gmx.de
// *
// * Date: 20.12.13
// * Time: 17:50
// *
// */
//class CompressorSpec
//  extends TestKit(ActorSystem("compressorSpecSystem"))
//  with DefaultTimeout with ImplicitSender
//  with WordSpecLike with Matchers with BeforeAndAfterAll {
//  val actorSystem = ActorSystem("compressorTest")
//  "A compressor object" when {
//    "fired with an erroneous compress msg" should {
//      "reply error on unsupported compressor" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        tto ! Compress(VFSHelper.createRandomFile("dir01/file01"), compressor = "klöölk")
//
//        expectMsg(1 second, OnCompressError(CompressError.UnsupportedCompressor, "supported compressor types: bzip2 => '.bz2', gz => '.gz'"))
//      }
//    }
//
//    "fired with an erroneous unCompress msg" should {
//      "reply error on unsupported compressor" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        tto ! UnCompress(VFSHelper.createRandomFile("dir01/file01"))
//
//        expectMsg(1 second, OnUnCompressError(UnCompressError.UnsupportedCompressor, "supported compressor types: bzip2 => '.bz2', gz => '.gz'"))
//      }
//    }
//
//    "compress to a bz2 archive" should {
//      "return a new file object with bz2 extension" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        tto ! Compress(VFSHelper.createRandomFile("dir01/file01"), CompressorStreamFactory.BZIP2)
//
//        val msg = receiveN(1, 1 second)
//
//        val archive = msg(0).asInstanceOf[OnCompressFinished].file
//        archive.getName.getExtension should be ("bz2")
//      }
//
//      "return a valid archive" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        val file1 = VFSHelper.createRandomFile("dir01/file01")
//        tto ! Compress(file1, CompressorStreamFactory.BZIP2)
//
//        val msg = receiveN(1, 1 second)
//
//        val archive = msg(0).asInstanceOf[OnCompressFinished].file
//        val bzIn = new BZip2CompressorInputStream(archive.getContent.getInputStream)
//
//        ChecksumTools.fromIS(bzIn) should be (ChecksumTools.fromVFSFile(file1))
//      }
//
//      "use bz2 as default compressor" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        val file1 = VFSHelper.createRandomFile("dir01/file01")
//        tto ! Compress(file1)
//
//        val msg = receiveN(1, 1 second)
//
//        val archive = msg(0).asInstanceOf[OnCompressFinished].file
//        val bzIn = new BZip2CompressorInputStream(archive.getContent.getInputStream)
//
//        archive.getName.getExtension should be ("bz2")
//        ChecksumTools.fromIS(bzIn) should be (ChecksumTools.fromVFSFile(file1))
//      }
//
//      "return a valid archive when compressed twice" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        val file1 = VFSHelper.createRandomFile("dir01/file01")
//        tto ! Compress(file1, CompressorStreamFactory.BZIP2)
//
//        receiveN(1, 1 second)
//
//        VFSHelper.writeToFile(file1, "kk")
//
//        tto ! Compress(file1, CompressorStreamFactory.BZIP2)
//
//        val msg = receiveN(1, 1 second)
//        val archive = msg(0).asInstanceOf[OnCompressFinished].file
//
//        val bzIn = new BZip2CompressorInputStream(archive.getContent.getInputStream)
//
//        ChecksumTools.fromIS(bzIn) should be (ChecksumTools.fromVFSFile(file1))
//      }
//    }
//
//    "compress to a gzip archive" should {
//      "return a new file object with gz extension" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        tto ! Compress(VFSHelper.createRandomFile("dir01/file01"), CompressorStreamFactory.GZIP)
//
//        val msg = receiveN(1, 1 second)
//
//        val archive = msg(0).asInstanceOf[OnCompressFinished].file
//        archive.getName.getExtension should be ("gz")
//      }
//
//      "return a valid archive" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        val file1 = VFSHelper.createRandomFile("dir01/file01")
//        tto ! Compress(file1, CompressorStreamFactory.GZIP)
//
//        val msg = receiveN(1, 1 second)
//
//        val archive = msg(0).asInstanceOf[OnCompressFinished].file
//        val bzIn = new GzipCompressorInputStream(archive.getContent.getInputStream)
//
//        ChecksumTools.fromIS(bzIn) should be (ChecksumTools.fromVFSFile(file1))
//      }
//    }
//
//    "unCompress from a bz2 archive" should {
//      "return a new file object without bz2 extension" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        val file1 = VFSHelper.createRandomFile("dir01/file01.so")
//        tto ! Compress(file1, CompressorStreamFactory.BZIP2)
//
//        val archiveMsg = receiveN(1, 1 second)
//        val archive = archiveMsg(0).asInstanceOf[OnCompressFinished].file
//
//        tto ! UnCompress(archive)
//        val plainMsg = receiveN(1, 1 second)
//        val plain = plainMsg(0).asInstanceOf[OnUnCompressFinished].file
//        plain.getName.getExtension should be ("so")
//      }
//
//      "return a valid file" in {
//        val tto = actorSystem.actorOf(Compressor.props())
//        val file1 = VFSHelper.createRandomFile("dir01/file01")
//        tto ! Compress(file1, CompressorStreamFactory.BZIP2)
//
//        val archiveMsg = receiveN(1, 1 second)
//        val archive = archiveMsg(0).asInstanceOf[OnCompressFinished].file
//        tto ! UnCompress(archive)
//
//        val plainMsg = receiveN(1, 1 second)
//        val plain = plainMsg(0).asInstanceOf[OnUnCompressFinished].file
//
//        ChecksumTools.fromVFSFile(plain) should be (ChecksumTools.fromVFSFile(file1))
//      }
//    }
//  }
//}
