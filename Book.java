/*
    Author: Tyler Gan
    Date: 1/10/20
    INFO1113 Assignment 1: Library Program

    This is the Book class, containing all methods specific to the Book object. It will be used in conjunction with the Member class to run the actual Library class which contains 
    the program itself; this contains methods which will only store information specific to a Book object.
*/

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

public class Book extends Library{
    //Book extends from Library to get my sortBook() methods 

    //constructor fields
    private String title;
    private String author;
    private String genre;
    private String serialNumber;

    //additional fields
    private Member renter;
    private ArrayList<Member> renterHist = new ArrayList<Member>();

    public Book(String title, String author, String genre, String serialNumber) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.serialNumber = serialNumber;
    }

    public String getTitle() {
        /*
            Returns the book title:
                Parameters --> None
                Returns --> Book title
        */
        
        return title;
    }

    public String getAuthor() {
        /*
            Returns the author of the book:
                Parameters --> None
                Returns --> Book author
        */

        return author;
    }

    public String getGenre() {
        /*
            Returns the genre of the book:
                Parameters --> None
                Returns --> Book genre
        */

        return genre;
    }

    public String getSerialNumber() {
        /*
            Returns the serial number of the book:
                Parameters --> None
                Returns --> Book serial number
        */

        return serialNumber;
    }

    public String longString() {
        /*
            Returns the long string format highlighting information regarding the book:
                Parameters --> None
                Returns --> the long string version of the book information -->  <serialnumber>: <title> (<author>, <genre>) <<rented by: <memberNumber>> || available>
        */

        if (isRented()) {
            return String.format("%1$s: %2$s (%3$s, %4$s)\nRented by: %5$s.", getSerialNumber(), getTitle(), getAuthor(), getGenre(), renter.getMemberNumber());
        } 

        return String.format("%1$s: %2$s (%3$s, %4$s)\nCurrently available.", getSerialNumber(), getTitle(), getAuthor(), getGenre());
    }

    public String shortString() {
        /*
            Returns the short string format highlighting information regarding the book:
                Parameters --> None
                Returns --> the short string version of the book information --> <title> (<author>)
        */

        return String.format("%1$s (%2$s)", getTitle(), getAuthor());
    }

    public List<Member> renterHistory() {
        /*
            Returns a list of those who rented the specific book:
                Parameters --> None
                Returns --> a List of previous renters
        */

        return renterHist;
    }

    public boolean isRented() {
        /*
            Determines whether the book is rented or not:
                Parameters --> None
                Returns --> a boolean 
        */

        return (!(renter == null));
    }

    public boolean rent (Member member) {
        /*
            Returns a boolean determining whether the book was successfully rented or not. It will set the renter to the member passed in the parameter.
                Parameters --> Member object
                Returns --> a Boolean
        */

        if (member == null || isRented()) {
            return false;
        } 
        
        renter = member;

        return true;
    }

    public boolean relinquish(Member member) {
        /*
            Returns a boolean determining whether the book was successfully relinquished or not.
                Parameters --> Member object
                Returns --> a Boolean
        */

        if (member == null || renter == null) {
            return false;
        } 

        if (!member.getMemberNumber().equals(renter.getMemberNumber())) {
            return false;
        }
        
        //book now has no renter, so renter gets added to this book's renter history.
        renter = null; 
        renterHist.add(member); 

        return true;
    }

    public static Book readBook(String filename, String serialNumber) {
        /*
            Returns a new Book object read from a CSV file.
                Parameters --> String filename, serialNumber;
                Returns --> a Book object
        */

        if (filename == null || serialNumber == null) {
            return null;
        } 

        try {
            File f = new File(filename);
            Scanner reader = new Scanner(f);

           reader.nextLine(); //skip first line as the first line is serialNumber,title,author,genre
            while (reader.hasNextLine()) {
                String[] info = reader.nextLine().split(",");
                String serial = info[0], title = info[1], author = info[2], genre = info[3];

                if (serial.equals(serialNumber)) {
                    reader.close();
                    return (new Book(title, author, genre, serialNumber));
                } 
            }
            reader.close();
            return null;

        } catch (FileNotFoundException e) {
            return null;
        }
    }
    public static List<Book> readBookCollection(String filename) {
        /*
            Returns a list of new Book objects read from a CSV file.
                Parameters --> String filename
                Returns --> a List of Book objects
        */

        if (filename == null) {
            return null;
        } 

        try {
            ArrayList<Book> books = new ArrayList<Book>();

            //similar method to readBook() except every book from CSV is added and put into a List.
            File f = new File(filename);
            Scanner reader = new Scanner(f);

            reader.nextLine(); 
            while (reader.hasNextLine()) {
                String[] info = reader.nextLine().split(",");
                String serial = info[0], title = info[1], author = info[2], genre = info[3];

                books.add(new Book(title, author, genre, serial));
            }
            reader.close();
            return books;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public static void saveBookCollection(String filename, Collection<Book> books) {    
        /*
            Writes to a specific file of all the current books located in the Library system.
                Parameters --> String filename, Collection Books
                Returns --> a Book object
        */

        if (filename == null || books == null) {
            return; 
        } 
        
        try {
            FileWriter fw = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("serialNumber,title,author,genre");
            for (Book book: books) {
                String append = "\n";
                append += String.format("%1$s,%2$s,%3$s,%4$s", book.getSerialNumber(), book.getTitle(), book.getAuthor(), book.getGenre());
                bw.write(append);
            }
            bw.close();
        } catch (IOException e) {
        }
    }
    
    public static List<Book> filterAuthor​(List<Book> books, String author) {
        /*
            Sorts a List of books by author.
                Parameters --> List books, String author
                Returns --> a List of Book objects
        */

        if (books == null || author == null) {
            return null;
        } 

        ArrayList<Book> filtered = new ArrayList<Book>();

        //Iterating through books to check if the author of the book matches our desired author
        for(Book book: books) {
            if (book == null) {
                return null;
            } 

            if (book.getAuthor().equals(author)) {
                filtered.add(book);
            }
        }
        return (sortBooks(filtered)); //sortBooks is a method taken from my Library class
    }

    public static List<Book> filterGenre​(List<Book> books, String genre) {
        /*
            Sorts a List of books by genre.
                Parameters --> List books, String genre
                Returns --> a List of Book objects
        */

        if (books == null || genre == null) {
            return null;
        }

        ArrayList<Book> filtered = new ArrayList<Book>();

        //Iterating through books to check if the genre of the book matches our desired genre
        for(Book book: books) {
            if (book == null) {
                return null;
            }

            if (book.getGenre().equals(genre)) {
                filtered.add(book);
            } 
        }
        return (sortBooks(filtered));
    }
    
}
