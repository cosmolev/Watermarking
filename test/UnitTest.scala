import models.{Book,Journal}
import models.Doc.md5
import org.scalatest.FunSuite

class UnitTest extends FunSuite  {

  test("Book hashes should be equal") {
    val book = Book("book","War and Peace","Lev Tolstoy",None,"novel")
    val hash = md5("book"+"War and Peace"+"Lev Tolstoy"+"novel")
    assert(models.Doc.md5(book) == hash)
  }

  test("Journal hashes should be equal") {
    val journal = Journal("journal","Journal of the American Revolution","Todd Andrlik",None)
    val hash = md5("journal"+"Journal of the American Revolution"+"Todd Andrlik")
    assert(models.Doc.md5(journal) == hash)
  }

}
