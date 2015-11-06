package models

import java.security.MessageDigest

import anorm._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.{JsValue, Json, Writes}
import anorm.SqlParser.scalar

trait Document {
  val content : String
  val title : String
  val author : String
  val watermark : Option[String]
}

case class Book(content: String = "book",title: String, author: String, watermark: Option[String], topic: String) extends Document
case class Journal(content : String = "journal", title: String, author: String, watermark : Option[String]) extends Document

object Doc {

  implicit val bookWrites = new Writes[Book]{
    def writes(b: Book) = Json.obj(
      "content" -> b.content,
      "title" -> b.title,
      "author" -> b.author,
      "watermark" -> b.watermark,
      "topic" -> b.topic
    )
  }

  implicit val journalWrites = new Writes[Journal]{
    def writes(j: Journal) = Json.obj(
      "content" -> j.content,
      "title" -> j.title,
      "author" -> j.author,
      "watermark" -> j.watermark
    )
  }

  implicit val documentWrites = new Writes[Document] {
    override def writes(d: Document): JsValue = d match {
      case b: Book => Json.toJson(b)(bookWrites)
      case j: Journal => Json.toJson(j)(journalWrites)
    }
  }

  def setWatermark(id: Int, watermark: String) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          update document
          set watermark = {watermark}
          where id = {id}
        """
      ).on(
        'id -> id,
        'watermark -> watermark
      ).executeUpdate()
    }
  }

  def createDocument(content: String, title: String, author: String, topic: Option[String]) : Int={
    val id:Int =
    DB.withConnection { implicit connection =>
        SQL("select next value for document_seq").as(scalar[Int].single)
    }
    DB.withConnection { implicit connection =>
      SQL(
        """
          insert into document values (
            {id},
            {content}, {title}, {author}, null, {topic}
          )
        """
      ).on(
        'content -> content,
        'title -> title,
        'author -> author,
        'topic -> topic,
        'id -> id
      ).executeUpdate()
    }
    id
  }

  def findById(id: Int): Document = {
    DB.withConnection { implicit connection =>
      val document = SQL("SELECT content, title, author, watermark, topic FROM document WHERE id = {id}").
        on('id -> id).as(patternParser.single)
      document
    }
  }

  def findAll(): List[Document] = {
    DB.withConnection { implicit connection =>
      val documents = SQL("SELECT content, title, author, watermark, topic FROM document").
        as(patternParser.*)
      documents
    }
  }

  val patternParser = RowParser[Document] {
    case Row(content:String, title: String, author: String, watermark: Option[String], topic: Option[String]) if topic.isDefined
    => Success(Book(content,title,author,watermark,topic.get))

    case Row(content:String, title: String, author: String, watermark: Option[String], topic: Option[String]) if topic.isEmpty
    => Success(Journal(content,title,author,watermark))
    case row => Error(TypeDoesNotMatch(s"unexpected: $row"))
  }

  def md5(s: String): String = {
    MessageDigest.getInstance("MD5").digest(s.getBytes).map(0xFF & _).map("%02x".format(_)).mkString
  }

  def md5(d: Document): String = d match {
    case b: Book => new String(md5(b.content+b.title+b.author+b.topic))
    case j: Journal => new String(md5(j.content+j.title+j.author))
  }
}