package org.sqlproc.sample.simple;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlproc.engine.SqlEngineFactory;
import org.sqlproc.engine.SqlSession;
import org.sqlproc.engine.jdbc.JdbcEngineFactory;
import org.sqlproc.engine.jdbc.JdbcSimpleSession;
import org.sqlproc.engine.util.DDLLoader;
import org.sqlproc.sample.simple.model.BankAccount;
import org.sqlproc.sample.simple.model.Contact;
import org.sqlproc.sample.simple.model.CreditCard;
import org.sqlproc.sample.simple.model.Library;
import org.sqlproc.sample.simple.model.Movie;
import org.sqlproc.sample.simple.model.NewBook;
import org.sqlproc.sample.simple.model.Person;
import org.sqlproc.sample.simple.model.PhoneNumber;
import org.sqlproc.sample.simple.model.Subscriber;
import org.sqlproc.sample.simple.type.PhoneNumberType;
import org.sqlproc.sample.simple.dao.BankAccountDao;
import org.sqlproc.sample.simple.dao.BookDao;
import org.sqlproc.sample.simple.dao.ContactDao;
import org.sqlproc.sample.simple.dao.CreditCardDao;
import org.sqlproc.sample.simple.dao.LibraryDao;
import org.sqlproc.sample.simple.dao.MovieDao;
import org.sqlproc.sample.simple.dao.PersonDao;
import org.sqlproc.sample.simple.dao.PersonLibraryDao;
import org.sqlproc.sample.simple.dao.SubscriberDao;

public class Main {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  private Connection connection;

  private SqlSession session;

  private SqlEngineFactory sqlFactory;

  private List<String> ddls;

  static {
    try {
      DriverManager.registerDriver(new org.hsqldb.jdbcDriver());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public Main() throws SQLException {
    JdbcEngineFactory factory = new JdbcEngineFactory();
    factory.setMetaFilesNames("statements.qry");
    factory.addCustomType(new PhoneNumberType());
    this.sqlFactory = factory;
    ddls = DDLLoader.getDDLs(this.getClass(), "hsqldb.ddl");
    connection = DriverManager.getConnection("jdbc:hsqldb:mem:sqlproc", "sa", "");
    session = new JdbcSimpleSession(connection);
  }

  public void setupDb() throws SQLException {
    Statement stmt = null;
    try {
      stmt = connection.createStatement();
      for (int i = 0, n = ddls.size(); i < n; i++) {
        String ddl = ddls.get(i);
        if (ddl == null) {
          continue;
        }
        System.out.println(ddl);
        stmt.addBatch(ddl);
      }
      stmt.executeBatch();
    }  finally {
      if (stmt != null) {
        stmt.close();
      }
    }
  }

  public NewBook insertNewBook(NewBook book) {
    SqlCrudEngine sqlInsertMedia = sqlFactory.getCrudEngine("INSERT_MEDIA");
    SqlCrudEngine sqlInsertNewBook = sqlFactory.getCrudEngine("INSERT_NEW_BOOK");
    int count = sqlInsertMedia.insert(session, book);
    if (count > 0) {
      sqlInsertNewBook.insert(session, book);
    }
    logger.info("insert book: " + count + ": " + book);
    return (count > 0) ? book : null;
  }

  public NewBook getNewBook(NewBook book) {
    SqlCrudEngine sqlEngine = sqlFactory.getCrudEngine("GET_NEW_BOOK");
    NewBook b = sqlEngine.get(session, NewBook.class, book);
    logger.info("get book: " + b);
    return b;
  }

  public static void main(String[] args) throws Exception {
    Person person, p;
    BankAccount bankAccount, b1;
    NewBook book, b;
    Contact contact, c;
    CreditCard creditCard, c1;
    Library l, library;
    Movie movie, m;
    Subscriber subscriber, s;
    List<Person> list;
    boolean deleted;
    Main main = new Main();
    main.setupDb();
    main.initDao();
    Person jan = main.getPersonDao().insertPerson(new Person("Jan", "J\u00e1nsk\u00fd"));
    main.getContactDao().insertPersonContacts(jan, new Contact()._setAddress("Jan address 1")._setPhoneNumber(new PhoneNumber(111, 222, 3333)));
    Person janik = main.getPersonDao().insertPerson(new Person("Jan\u00edk", "Jan\u00ed\u010dek"));
    main.getContactDao().insertPersonContacts(janik, new Contact()._setAddress("Janik address 1"));
    Person honza = main.getPersonDao().insertPerson(new Person("Honza", "Honzovsk\u00fd"));
    main.getContactDao().insertPersonContacts(honza, new Contact()._setAddress("Honza address 1"), new Contact()._setAddress("Honza address 2"));
    Person honzik = main.getPersonDao().insertPerson(new Person("Honzik", "Honz\u00ed\u010dek"));
    Person andrej = main.getPersonDao().insertPerson(new Person("Andrej", "Andrej\u010dek")._setSsn("123456789"));
    main.getContactDao().insertPersonContacts(andrej, new Contact()._setAddress("Andrej address 1")._setPhoneNumber(new PhoneNumber(444, 555, 6666)));
    Library lib = main.getLibraryDao().insertLibrary(new Library("Alexandria Library"));
    Subscriber janikS = main.getSubscriberDao().insertLibrarySubscriber(lib, new Subscriber(lib, "Janik Subscr")._setContact(jan.getContacts().get(0)));
    Subscriber honzaS = main.getSubscriberDao().insertLibrarySubscriber(lib, new Subscriber(lib, "Honza Subscr")._setContact(honza.getContacts().get(0)));
    BankAccount bankAccount1 = main.getBankAccountDao().insertBankAccount(new BankAccount(janikS, "BA")._setBaAccount("account 1"));
    main.getBankAccountDao().insertBankAccount(new BankAccount(honzaS, "BA")._setBaAccount("account 2"));
    CreditCard creditCard1 = main.getCreditCardDao().insertCreditCard(new CreditCard(janikS, "CC")._setCcNumber(123L));
    main.getCreditCardDao().insertCreditCard(new CreditCard(honzaS, "CC")._setCcNumber(456L));
    NewBook book1 = main.getBookDao().insertNewBook(new NewBook("The Adventures of Robin Hood", "978-0140367003"));
    NewBook book2 = main.getBookDao().insertNewBook(new NewBook("The Three Musketeers", "978-1897093634"));
    Movie movie1 = main.getMovieDao().insertMovie(new Movie("Pippi L\u00e5ngstrump i S\u00f6derhavet", "abc", 82));
    Movie movie2 = main.getMovieDao().insertMovie(new Movie("Die Another Day", "def", 95));
    main.getPersonLibraryDao().insertPersonLibrary(jan, book1, movie1);
    main.getPersonLibraryDao().insertPersonLibrary(honza, book2, movie2);
    main.getPersonLibraryDao().insertPersonLibrary(andrej, book1, book2, movie2);
    person = new Person();
    person.setId(andrej.getId());
    person.setFirstName("Andrej\u00edk");
    p = main.getPersonDao().updatePerson(person);
    Assert.assertNotNull(p);
    bankAccount1.setBaAccount("updated account");
    bankAccount1.setSubscriber(honzaS);
    b1 = main.getBankAccountDao().updateBankAccount(bankAccount1);
    Assert.assertNotNull(b1);
    book1.setIsbn("978-9940367003");
    book1.setTitle("The Adventures of Robin Hood Updated");
    b = main.getBookDao().updateBook(book1);
    Assert.assertNotNull(b);
    contact = honza.getContacts().get(0);
    contact.setAddress("Honza address 1 Updated");
    contact.setPhoneNumber(new PhoneNumber(000, 000, 0000));
    c = main.getContactDao().updateContact(contact);
    Assert.assertNotNull(c);
    creditCard1.setType("DD");
    c1 = main.getCreditCardDao().updateCreditCard(creditCard1);
    Assert.assertNotNull(c1);
    lib.setName("Alexandria Library Updated");
    l = main.getLibraryDao().updateLibrary(lib);
    Assert.assertNotNull(c);
    movie1.setUrlimdb("def Updated");
    movie1.setTitle("Die Another Day Updated");
    m = main.getMovieDao().updateMovie(movie1);
    Assert.assertNotNull(m);
    janikS.setName("Janik Subscr Updated");
    s = main.getSubscriberDao().updateSubscriber(janikS);
    Assert.assertNotNull(s);
    person = new Person();
    person.setId(andrej.getId());
    p = main.getPersonDao().getPerson(person);
    Assert.assertNotNull(p);
    Assert.assertEquals("Andrej\u00edk", p.getFirstName());
    Assert.assertEquals("Andrej\u010dek", p.getLastName());
    Assert.assertEquals("123456789", p.getSsn());
    person = new Person();
    person.setId(andrej.getId());
    person.setFirstName("Andrio\u0161a");
    person.setNull(Person.Attribute.ssn);
    p = main.getPersonDao().updatePerson(person);
    Assert.assertNotNull(p);
    person = new Person();
    person.setId(andrej.getId());
    p = main.getPersonDao().getPerson(person);
    Assert.assertNotNull(p);
    Assert.assertEquals("Andrio\u0161a", p.getFirstName());
    Assert.assertEquals("Andrej\u010dek", p.getLastName());
    Assert.assertNull(p.getSsn());
    book = new NewBook();
    book.setId(book1.getId());
    b = main.getBookDao().getNewBook(book);
    Assert.assertNotNull(b);
    Assert.assertEquals("978-9940367003", b.getNewIsbn());
    Assert.assertEquals("The Adventures of Robin Hood Updated", b.getTitle());
    bankAccount = new BankAccount();
    bankAccount.setId(bankAccount1.getId());
    b1 = main.getBankAccountDao().getBankAccount(bankAccount);
    Assert.assertNotNull(b1);
    Assert.assertEquals("updated account", b1.getBaAccount());
    Assert.assertEquals(honzaS.getId(), b1.getSubscriber().getId());
    contact = new Contact();
    contact.setId(honza.getContacts().get(0).getId());
    c = main.getContactDao().getContact(contact);
    Assert.assertNotNull(c);
    Assert.assertEquals("Honza address 1 Updated", c.getAddress());
    Assert.assertEquals(new PhoneNumber(000, 0000, 0000), c.getPhoneNumber());
    creditCard = new CreditCard();
    creditCard.setId(creditCard1.getId());
    c1 = main.getCreditCardDao().getCreditCard(creditCard);
    Assert.assertNotNull(c1);
    Assert.assertEquals("DD", c1.getType());
    library = new Library();
    library.setId(lib.getId());
    l = main.getLibraryDao().getLibrary(library);
    Assert.assertNotNull(l);
    movie = new Movie();
    movie.setId(movie1.getId());
    m = main.getMovieDao().getMovie(movie);
    Assert.assertNotNull(m);
    Assert.assertEquals("def Updated", m.getUrlimdb());
    Assert.assertEquals("Die Another Day Updated", m.getTitle());
    subscriber = new Subscriber();
    subscriber.setId(janikS.getId());
    s = main.getSubscriberDao().getSubscriber(subscriber);
    Assert.assertNotNull(s);
    Assert.assertEquals("Janik Subscr Updated", s.getName());
    person = new Person();
    person.setId(andrej.getId());
    person.setInit(Person.Association.contacts);
    p = main.getPersonDao().getPerson(person);
    Assert.assertNotNull(p);
    Assert.assertEquals("Andrej\u010dek", p.getLastName());
    Assert.assertTrue(p.getContacts().size() == 1);
    System.out.println("Contact for Andrej " + p.getContacts().get(0));
  }

  public void initDao() throws SQLException {
    bankAccountDao = new BankAccountDao(session, sqlFactory);
    bookDao = new BookDao(session, sqlFactory);
    contactDao = new ContactDao(session, sqlFactory);
    creditCardDao = new CreditCardDao(session, sqlFactory);
    libraryDao = new LibraryDao(session, sqlFactory);
    movieDao = new MovieDao(session, sqlFactory);
    personDao = new PersonDao(session, sqlFactory);
    personLibraryDao = new PersonLibraryDao(session, sqlFactory);
    subscriberDao = new SubscriberDao(session, sqlFactory);
  }

  private BankAccountDao bankAccountDao;

  private BookDao bookDao;

  private ContactDao contactDao;

  private CreditCardDao creditCardDao;

  private LibraryDao libraryDao;

  private MovieDao movieDao;

  private PersonDao personDao;

  private PersonLibraryDao personLibraryDao;

  private SubscriberDao subscriberDao;

  public BankAccountDao getBankAccountDao() {
    return bankAccountDao;
  }

  public BookDao getBookDao() {
    return bookDao;
  }

  public ContactDao getContactDao() {
    return contactDao;
  }

  public CreditCardDao getCreditCardDao() {
    return creditCardDao;
  }

  public LibraryDao getLibraryDao() {
    return libraryDao;
  }

  public MovieDao getMovieDao() {
    return movieDao;
  }

  public PersonDao getPersonDao() {
    return personDao;
  }

  public SubscriberDao getSubscriberDao() {
    return subscriberDao;
  }

  public PersonLibraryDao getPersonLibraryDao() {
    return personLibraryDao;
  }
}