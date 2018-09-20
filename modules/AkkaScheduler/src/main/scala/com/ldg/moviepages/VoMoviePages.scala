package com.ldg.moviepages

import java.io.File
import com.ldg.utilities._
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.jsoup.nodes.Document
import scala.util.{Failure, Success, Try}

/**
  * ref : http://jsoup.org/cookbook/extracting-data/selector-syntax
  *
  * https://github.com/typesafehub/scala-logging
  *
  */

case class VoMoviePages(urlLinkOrFile: Any) extends ParseableMovieDefinition with ScalaLogger{

  /**
    * Partial function that can accept two types of parameters:
    *
    * 	1- url: connect to the url to read it
    * 	2- file: read the file(mut be html file or the content must be like an html file)
    *
    */
  val partialUrlOrFile: PartialFunction[Any, String] = {
    case urlparam @ _ => urlparam match {
      case param: String =>  param
      case file: File => file.getAbsolutePath
    }
  }
  val url:String = partialUrlOrFile(urlLinkOrFile)

  /** Document is the HTML page modeled as an Object. */
  val getDocument: PartialFunction[Any, Try[Document]] = {

    case urlparam @ _ => urlparam match {
      case urlparam: String => {
        Try(
          //can deal an IOException
          org.jsoup.Jsoup.connect(urlparam).
            timeout(100 * 1000).userAgent("Chrome 41.0.2228.0").get()
        )

      }
      case urlparam: File => Try(
        //can deal an IOException
        Jsoup.parse(urlparam,"UTF-8", "")
      )
    }
  }

  def getSpecificElement(tag: String, tagNum: Int)(documents: Try[Document]): Either[FailureTrait, Option[String]] =
    documents match {
      case Success(document) => {
        val tagInClass: Elements = document.select(tag)
        if (tagInClass.size() > tagNum)
        {Right(Some(tagInClass.html()))}
        else {
          log.error("Nonexistent tag[{}] exception, url:[{}] was NOT Parsed.", tag,url)
          Left(AnyFailure(tag, "Nonexistent tag exception"))
        }
      }
      case Failure(e) =>
        val errMessage = e.getMessage
        /** java.net.UnknownHostException */
        log.error("Jsoup IOException:{}, url:[{}] was NOT Parsed.", errMessage,url)
        Left(JsoupFailure(tag, s"Jsoup IOException: $errMessage"))
    }

  def buildMovieThing: Either[FailureTrait, TitlesMoviePage] = {
    val MejorEnEvoPageHtml: Try[Document] = getDocument(urlLinkOrFile)
    for {
      title <- getSpecificElement("title", 0)(MejorEnEvoPageHtml).right
      meta <- getSpecificElement("meta", 0)(MejorEnEvoPageHtml).right
    } yield {
      TitlesMoviePage(title.getOrElse("EmptyInner HTML"), meta.getOrElse("EmptyInner Meta"), url)
    }
  }

  def parseMoviePages: Result = {
    buildMovieThing match {
      case Right(titlesMoviePage) => Result(None,Some(titlesMoviePage))
      case Left(fail) => Result(Some(fail),None)
    }
  }

  val parsedPages= parseMoviePages
  val parsedPagesOK = parsedPages.moviePage
  val parsedPagesWRONG = parsedPages.failures

}