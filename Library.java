/*
    Author: Tyler Gan
    Date: 1/10/20
    INFO1113 Assignment 1: Library Program

    This is the Library class, which contains all methods specific to this Library class. It uses methods from the Book and Member class to store important information specific
    to each of those objects; this is the engine of the entire program where the user will interact with the program itself. This class is a base-class for the Book and Member class
    as the Library class contains the sort method used to sort books in serial number format (ascending).
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Library {
    
    public static final String HELP_STRING = "EXIT ends the library process\n" + 
                                             "COMMANDS outputs this help string\n\n" +

                                             "LIST ALL [LONG] outputs either the short or long string for all books\n" +
                                             "LIST AVAILABLE [LONG] outputs either the short of long string for all available books\n" +
                                             "NUMBER COPIES outputs the number of copies of each book\n" +
                                             "LIST GENRES outputs the name of every genre in the system\n" +
                                             "LIST AUTHORS outputs the name of every author in the system\n\n" +

                                             "GENRE <genre> outputs the short string of every book with the specified genre\n" + 
                                             "AUTHOR <author> outputs the short string of every book by the specified author\n\n" + 
                                             "BOOK <serialNumber> [LONG] outputs either the short or long string for the specified book\n" +
                                             "BOOK HISTORY <serialNumber> outputs the rental history of the specified book\n\n" + 

                                             "MEMBER <memberNumber> outputs the information of the specified member\n" +
                                             "MEMBER BOOKS <memberNumber> outputs the books currently rented by the specified member\n" + 
                                             "MEMBER HISTORY <memberNumber> outputs the rental history of the specified member\n\n" +

                                             "RENT <memberNumber> <serialNumber> loans out the specified book to the given member\n" + 
                                             "RELINQUISH <memberNumber> <serialNumber> returns the specified book from the member\n" + 
                                             "RELINQUISH ALL <memberNumber> returns all books rented by the specified member\n\n" +
                                             
                                             "ADD MEMBER <name> adds a member to the system\nADD BOOK <filename> <serialNumber> adds a book to the system\n\n" + 

                                             "ADD COLLECTION <filename> adds a collection of books to the system\n" + 
                                             "SAVE COLLECTION <filename> saves the system to a csv file\n\n" +

                                             "COMMON <memberNumber1> <memberNumber2> ... outputs the common books in members\' history";
    
    //Additional fields 
    List<Book> allBooks = new ArrayList<>(), availBooks = new ArrayList<>();
    List<Member> members = new ArrayList<>();
    private int currSerialNum = 100000;

    public void getAllBooks(boolean fullString) {
        /*
            Gets all the books in the Library system and ouputs its information in long or short string format
                Parameters --> boolean fullString
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        }

        if (fullString) {
            //doing this in a for loop rather than using Streams because each long-format is separated by a newline character.
            for (int i = 0; i < allBooks.size(); i++) {
                if (i != allBooks.size() - 1) {
                    System.out.println(allBooks.get(i).longString() + "\n");
                } else {
                    System.out.println(allBooks.get(i).longString());
                } 
            }
        } else {
            allBooks.stream().forEach(b -> System.out.println(b.shortString()));
        } 
    }

    public void getAvailableBooks(boolean fullString) {
        /*
            Gets all the available books in the Library system and ouputs its information in long or short string format
                Parameters --> boolean fullString
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        if (availBooks.size() == 0) {
            System.out.println("No books available.");
            return;
        }

        List<Book> filtered = sortBooks(availBooks);

        //reason for using for loop is the samre as in the getAllBooks() method
        if (fullString) {
            for (int i = 0; i < filtered.size(); i++) {
                if (i != filtered.size() - 1) {
                    System.out.println(filtered.get(i).longString() + "\n");
                } else {
                    System.out.println(filtered.get(i).longString());
                } 
            }
        } else {
            filtered.stream().forEach(b -> System.out.println(b.shortString()));
        } 
    }

    public void getCopies() {
        /*
            Gets the number of copies of a particular book in a Library system and outputs it to STDOUT.
                Parameters --> Nothing
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        Map<String,Integer> copies = new HashMap<>();
        for (Book book: allBooks) {    
            copies.putIfAbsent(book.shortString(), 0);
            copies.computeIfPresent(book.shortString(), (k, v) -> v += 1);
        }
        //TreeMap sorts lexicographically
        Map<String,Integer> sortedCopies = new TreeMap<>(copies);
        for (Map.Entry<String,Integer> entry: sortedCopies.entrySet()) {
            System.out.printf("%s: %d\n", entry.getKey(), entry.getValue());
        }
    }

    public void getGenres() {
        /*
            Gets all the genres present in the library and outputs these genres to STDOUT.
                Parameters -->  Nothing
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        Set<String> sorted = new TreeSet<>(); 
        allBooks.stream().forEach(b -> sorted.add(b.getGenre())); //adding each book to the TreeSet using Streams
        sorted.stream().forEach(s -> System.out.println(s));
    }

    public void getAuthors() {
        /*
            Gets all the authors present in the library and outputs these authors to STDOUT.
                Parameters -->  Nothing
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        Set<String> sorted = new TreeSet<>(); 
        allBooks.stream().forEach(b -> sorted.add(b.getAuthor()));
        sorted.stream().forEach(s -> System.out.println(s));
    }

    public void getBooksByGenre(String genre) {
        /*
            Gets all the books genres in the library by genre and outputs these books to STDOUT.
                Parameters --> String genre
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        Set<String> check = new HashSet<>(); //done to see if the book is duplicate.
        Set<Book> books = new HashSet<>();

        for (Book book: allBooks) {
            if ((book.getGenre().toLowerCase().equals(genre.toLowerCase()) && (check.add(book.shortString())))) {
                books.add(book);
            } 
        }

        if (books.size() == 0) {
            System.out.printf("No books with genre %s.\n", genre);
            return;
        }

        List<Book> sorted = sortBooks(new ArrayList<>(books));
        sorted.stream().forEach(b -> System.out.println(b.shortString()));
    }

    public void getBooksByAuthor(String author) {
        /*
            Gets all the books in the library by author and outputs these books to STDOUT.
                Parameters --> String author
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        Set<String> check = new HashSet<>(); //checking for duplicates
        Set<Book> books = new HashSet<>();
        
        for (Book book: allBooks) {
            if (book.getAuthor().equals(author) && check.add(book.shortString())) {
                books.add(book);
            } 
        }

        if (books.size() == 0) {
            System.out.printf("No books by %s.\n", author);
            return;
        }

        List<Book> sorted = sortBooks(new ArrayList<>(books));
        sorted.stream().forEach(b -> System.out.println(b.shortString()));
    }

    public void getBook(String serialNumber, boolean fullString) {
        /*
            Gets a single specified book, searches for it in the library system and print out its information in either long or short string format.
                Parameters --> String serialNumber, boolean fullString
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        for (Book book: allBooks) {
            if (book.getSerialNumber().equals(serialNumber)) {
                if (fullString) {
                    System.out.println(book.longString());
                } else {
                    System.out.println(book.shortString());
                } 
                return;
            }
        }
        System.out.println("No such book in system.");
    }

    public void bookHistory(String serialNumber) {
        /*
            Gets a single specified book, searches for it in the library system and print out its history of renters.
                Parameters --> String serialNumber
                Returns --> Nothing
        */

        for (Book book: allBooks) {
            if (book.getSerialNumber().equals(serialNumber)) {
                if (book.renterHistory().size() == 0) {
                    System.out.println("No rental history.");
                }
                else {
                    book.renterHistory().stream().forEach(m -> System.out.println(m.getMemberNumber()));
                } 
                return;
            }
        }
        System.out.println("No such book in system.");
    }
    
    public void addBook(String bookFile, String serialNumber) {
        /*
            Adds a book from a CSV file.
                Parameters --> String bookFile, serialNumber
                Returns --> Nothing
        */

        try {
            File f = new File(bookFile);
            Scanner reader = new Scanner(f);
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("No such file.");
            return;
        }

        Book book = Book.readBook(bookFile, serialNumber);

        if (book == null) {
            System.out.println("No such book in file.");
            return;
        }
        if (allBooks.stream().anyMatch(b -> b.getSerialNumber().equals(serialNumber))) {
            System.out.println("Book already exists in system.");
            return;
        }
        allBooks.add(book);
        availBooks.add(book);
        System.out.printf("Successfully added: %s.\n", book.shortString());
    }

    public void rentBook(String memberNumber, String serialNumber) {
        /*
            Method which allows a member to rent a specific book
                Parameters --> String memberNumber, serialNumber
                Returns --> Nothing
        */

        if (memberCount()) {
            return;
        }
        if (bookCount()) {
            return;
        } 

        //iterate through all members in the system, find that member and allow them to rent the book.
        for (Member member: members) {
            if (member.getMemberNumber().equals(memberNumber)) {
                for (Book book: allBooks) {
                    if (book.getSerialNumber().equals(serialNumber)) {
                        if (member.rent(book)) {
                            availBooks.remove(book);
                            System.out.println("Success.");
                        } else {
                            System.out.println("Book is currently unavailable.");
                        }
                        return;
                    }
                }
                System.out.println("No such book in system.");
                return;
            }
        }
        System.out.println("No such member in system.");
        return;
    }

    public void relinquishBook(String memberNumber, String serialNumber) {
        /*
            Method that allows a member to return a book
                Parameters --> String bookFile, serialNumber
                Returns --> Nothing
        */

        if (memberCount()) {
            return;
        }
        if (bookCount()) {
            return;
        } 

        //stream methods to determine if the member or book exists in the system.
        if (!members.stream().anyMatch(m -> m.getMemberNumber().equals(memberNumber))) {
            System.out.println("No such member in system.");
            return;
        }

        if (!allBooks.stream().anyMatch(s -> s.getSerialNumber().equals(serialNumber))) {
            System.out.println("No such book in system.");
            return;
        }

        //iterate through current members in system and then get that member to relinquish the specfic book.
        for (Member member: members) {
            if (member.getMemberNumber().equals(memberNumber)) {
                if (!member.renting().stream().anyMatch(s -> s.getSerialNumber().equals(serialNumber))) {
                    System.out.println("Unable to return book.");
                    return;
                }
                
                //iterate through all books the member is currently renting
                for (Book book: member.renting()) {
                    if (book.getSerialNumber().equals(serialNumber)) {
                        member.relinquish(book);
                        availBooks.add(book);
                        System.out.println("Success.");
                        return;
                    }
                }
            }
        }
    }
    
    public void relinquishAll(String memberNumber) {
        /*
            Method that allows a specified member to return all their currently rented books.
                Parameters --> String bookFile, serialNumber
                Returns --> Nothing
        */
        
        if (memberCount()) {
            return;
        }

        for (Member member: members) {
            if (member.getMemberNumber().equals(memberNumber)) {
                availBooks.addAll(member.renting());
                member.relinquishAll();
                System.out.println("Success.");
                return;
            }
        }
        System.out.println("No such member in system.");
    }

    public void getMember(String memberNumber) {
        /*
            Method that prints specific information regarding that member and prints it to STDOUT.
                Parameters --> String memberNumber
                Returns --> Nothing
        */

        if (memberCount()) {
            return;
        }

        for (Member member: members) {
            if (member.getMemberNumber().equals(memberNumber)) {
                System.out.printf("%s: %s\n", member.getMemberNumber(), member.getName());
                return;
            }
        }
        System.out.println("No such member in system.");
    }

    public void getMemberBooks(String memberNumber) {
        /*
            Method that obtains all the member's currently rented books and prints it out to STDOUT.
                Parameters --> String memberNumber
                Returns --> Nothing
        */

        if (memberCount()) {
            return;
        }

        for (Member member: members) {
            if (member.getMemberNumber().equals(memberNumber)) {
                if (member.renting().size() == 0) {
                    System.out.println("Member not currently renting.");
                } else {
                    member.renting().stream().forEach(b -> System.out.println(b.shortString()));
                }
                return;
            }
        }
        System.out.println("No such member in system.");
    }

    public void memberRentalHistory(String memberNumber) {
        /*
            Method that obtains all the member's previously rented books and prints it out to STDOUT.
                Parameters --> String memberNumber
                Returns --> Nothing
        */

        if (memberCount()) {
            return;
        }

        for (Member member: members) {
            if (member.getMemberNumber().equals(memberNumber)) {
                if (member.history().size() == 0) {
                    System.out.println("No rental history for member.");
                } else {
                    member.history().stream().forEach(b -> System.out.println(b.shortString()));
                }
                return;
            }
        }
        System.out.println("No such member in system.");
    }

    public void addMember(String name) {
        /*
            Method that adds a new member to the system.
                Parameters --> String name
                Returns --> Nothing
        */

        Member member = new Member(name, Integer.toString(currSerialNum));
        members.add(member);
        currSerialNum++;
        System.out.println("Success.");
    }
    
    public void saveCollection(String filename) {
        /*
            Method that saves all the books in the system to a file.
                Parameters --> String filename
                Returns --> Nothing
        */

        if (bookCount()) {
            return;
        } 

        Book.saveBookCollection(filename, allBooks);
        System.out.println("Success.");
    }

    public void addCollection(String filename) {
        /*
            Method that adds all the books in a file to the library system.
                Parameters --> String filename
                Returns --> Nothing
        */

        List<Book> readBooks = Book.readBookCollection(filename);
        if (readBooks == null) {
            System.out.println("No such collection.");
            return;
        }

        Set<String> serialNums = new HashSet<>(); allBooks.stream().forEach(b -> serialNums.add(b.getSerialNumber())); //adding all serial numbers from allBooks arraylist to hashset
        List<Book> filtered = new ArrayList<>();

        for (Book book: readBooks) {
            //checking if the book has not already been added to the Library system.
            if (serialNums.add(book.getSerialNumber())) {
                filtered.add(book);
            } 
        }
        if (filtered.size() == 0) {
            System.out.println("No books have been added to the system.");
        } else {
            allBooks.addAll(filtered);
            availBooks.addAll(filtered);
            System.out.printf("%d books successfully added.\n", filtered.size());
        }
    }

    public void common(String[] memberNumbers) {
        /*
            Method that finds the intersection of all rented books between a given array of members and prints it to STDOUT
                Parameters --> String[] members
                Returns --> Nothing
        */

        if (memberCount()) {
            return;
        }
        if (bookCount()) {
            return;
        } 

        Member[] checkMembers = new Member[memberNumbers.length]; //called checkMembers as "members" name used to store all Members for Library system
        Set<String> checkDups = new HashSet<>();

        for (int i = 0; i < memberNumbers.length; i++) {
            String memberNum = memberNumbers[i];

            if (!members.stream().anyMatch(m -> m.getMemberNumber().equals(memberNum))) {
                System.out.println("No such member in system.");
                return;
            }

            if (!checkDups.add(memberNum)) {
                System.out.println("Duplicate members provided.");
                return;
            }
        
            //find matching member to memberNumber and add to checkMembers
            for (Member member: members) {
                if (member.getMemberNumber().equals(memberNum)) {
                    checkMembers[i] = member;
                    break;
                }
            }
        }

        List<Book> common = Member.commonBooks(checkMembers);

        if (common.size() == 0) {
            System.out.println("No common books.");
        } else {
            common.stream().forEach(b -> System.out.println(b.shortString()));
        } 
    }

    public void run() {
        /*
            A run method for the Library system. Simply runs the Library system.
                Parameters --> Nothing
                Returns --> Nothing
        */

        Scanner scanner = new Scanner(System.in);
        boolean finished = false;

        while (!finished) {
            System.out.print("user: ");
            String[] input = scanner.nextLine().split(" ");
            String cmd = input[0].toLowerCase(); //only doing lowercase to first command in case of input of name i.e. Add Member Tyler
            
            boolean checkLong = input.length > 2;

            //checking what command was received.
            try {
                switch(cmd) {
                    case "exit":
                        finished = true;
                        System.out.println("Ending Library process.");
                        break;
    
                    case "commands":
                        System.out.println(HELP_STRING);
                        break;
                    
                    case "list":
                        if (input[1].toLowerCase().equals("all")) {
                            //checking to see if an additional field was given (i.e. list all long)
                            if (checkLong) {
                                if (input[2].toLowerCase().equals("long")) {
                                    getAllBooks(true);
                                } else {
                                    getAllBooks(false);
                                } 
                            } else {
                                getAllBooks(false);
                            } 
                        } else if (input[1].toLowerCase().equals("available")) {
                            if (checkLong) {
                                if (input[2].toLowerCase().equals("long")) {
                                    getAvailableBooks(true);
                                } else {
                                    getAvailableBooks(false);
                                } 
                            } else {
                                getAvailableBooks(false);
                            } 
                        } else if (input[1].toLowerCase().equals("genres")) {
                            getGenres();
                        } else if (input[1].toLowerCase().equals("authors")) {
                            getAuthors();
                        }
                        break;
                    
                    case "number":
                        getCopies();
                        break;
                    
                    case "genre":
                        String genre = "";

                        //Adding genre with spaces i.e. Historical Fiction
                        for (int i = 1; i < input.length; i++) {
                            if (i != input.length - 1) {
                                genre += input[i] + " ";
                            } else {
                                genre += input[i];
                            } 
                        }

                        getBooksByGenre(genre);
                        break;
                    
                    case "author":
                        String author = "";

                        //Adding author with spaces i.e. J.K Rowling
                        for (int i = 1; i < input.length; i++) {
                            if (i != input.length - 1) {
                                author += input[i] + " ";
                            } else {
                                author += input[i];
                            } 
                        }

                        getBooksByAuthor(author);
                        break;
                    
                    case "book":
                        if (input[1].toLowerCase().equals("history")) {
                            bookHistory(input[2]);
                        } else {
                            if (checkLong) {
                                if (input[2].toLowerCase().equals("long")) {
                                    getBook(input[1], true);
                                } else {
                                    getBook(input[1], false);
                                } 
                            } else {
                                getBook(input[1], false);
                            } 
                        }
                        break;
                    
                    case "member":
                        if (input[1].toLowerCase().equals("books")) {
                            getMemberBooks(input[2]);
                        } else if (input[1].toLowerCase().equals("history")) {
                            memberRentalHistory(input[2]);
                        } else {
                            getMember(input[1]);
                        } 
                        break;
                    
                    case "rent":
                        rentBook(input[1], input[2]);
                        break;
                    
                    case "relinquish":
                        if (input[1].toLowerCase().equals("all")) {
                            relinquishAll(input[2]);
                        } else {
                            relinquishBook(input[1], input[2]);
                        }
                        break;
                    
                    case "add":
                        if (input[1].toLowerCase().equals("member")) {
                            String name = "";

                            //adding member with a name that has spaces i.e. Sherlock Holmes
                            for (int i = 2; i < input.length; i++) {
                                if (i != input.length - 1) {
                                    name += input[i] + " ";
                                } else {
                                    name += input[i];
                                } 
                            }

                            addMember(name);
                        } else if (input[1].toLowerCase().equals("book")) {
                            addBook(input[2], input[3]);
                        } else if (input[1].toLowerCase().equals("collection")) {
                            addCollection(input[2]);
                        } 
                        break;
                    
                    case "save":
                        saveCollection(input[2]);
                        break;
                    
                    case "common":
                        String[] members= new String[input.length - 1];

                        //adding member numbers to an array.
                        for (int i = 0; i < members.length; i++) {
                                members[i] = input[i + 1];
                            }

                        common(members);
                        break;
                    
                    default:
                        System.out.println("Ending Library process.");
                        finished = true;
                }
            } catch (ArrayIndexOutOfBoundsException e) {}

            if (!finished) {
                System.out.println("");
            } 
        }
        scanner.close();
    }

    public static void main(String[] args) {
        Library library = new Library();
        library.run();
    }

    //additional methods
    protected static List<Book> sortBooks(List<Book> ls) {
        /*
            A method that sorts the books in the Library by serialNumbers.
                Parameters --> List of Book objects
                Returns --> a sorted list of books by serialNumber.
        */

        ls.sort(Comparator.comparing(Book::getSerialNumber)); //generating a comparator method to sort Book by getSerialNumber()
        return ls;
    }

    private boolean bookCount() {
        /*
            A method used repeatedly throughout code where if there are no books, we simply print a message to STDOUT.
                Parameters --> Nothing
                Returns --> Nothing
        */

        if (allBooks.size() == 0) {
            System.out.println("No books in system.");
        } 
        return (allBooks.size() == 0);
    }

    private boolean memberCount() {
        /*
            A method used repeatedly throughout code where if there are no members, we simply print a message to STDOUT.
                Parameters --> Nothing
                Returns --> Nothing
        */

        if (members.size() == 0) {
            System.out.println("No members in system.");
        } 
        return (members.size() == 0);
    }

}