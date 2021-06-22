/*
    Author: Tyler Gan
    Date: 1/10/20
    INFO1113 Assignment 1: Library Program

    This is the Member class, containing all methods specific to the Member object. It will be used in conjunction with the Book class to run the actual Library class which contains 
    the program itself; this contains methods which will only store information specific to a Member object.
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Member extends Library {
    
    //constructor fields
    private String name;
    private String memberNumber;

    //additional fields
    private List<Book>currRented = new ArrayList<>();
    private List<Book>rentedHist = new ArrayList<>();

    public Member(String name, String memberNumber) {
        this.name = name;
        this.memberNumber = memberNumber;
    }

    public String getName() {
        /*
            A method that returns the name of the Member.
                Parameters --> Nothing
                Returns --> member name
        */

        return name;
    }

    public String getMemberNumber() {
        /*
            A method that returns the member number of the Member.
                Parameters --> Nothing
                Returns --> member number
        */

        return memberNumber;
    }

    public boolean rent(Book book) {
        /*
            A method that allows the member to rent a book; this returns a boolean to determine if the renting action was a success or failure.
                Parameters --> Book
                Returns --> boolean
        */

        if (book == null) {
            return false;
        } 

        if (book.isRented()) {
            return false;
        }

        currRented.add(book);
        return book.rent(this);
    }

    public boolean relinquish(Book book) {
        /*
            A method that allows the member to relinquish a book; this returns a boolean to determine if the renting action was a success or failure.
                Parameters --> Book
                Returns --> boolean
        */

        if (book == null) {
            return false;
        } 

        //checking for no serial number matches
        if (!currRented.stream().anyMatch(b -> b.getSerialNumber().equals(book.getSerialNumber()))) {
            return false; 
        } 

        rentedHist.add(book); 
        currRented.remove(book);
        return book.relinquish(this);
    }

    public void relinquishAll() {
        /*
            A method that allows the member to relinquish all their books.
                Parameters --> None
                Returns --> None
        */

        for (Book book: currRented) {
            book.relinquish(this);
        }

        rentedHist.addAll(currRented);
        currRented.clear();
    }

    public List<Book> history() {
        /*
            A method that retuns a List of previously rented books by the member.
                Parameters --> None
                Returns --> None
        */

        return rentedHist;
    }

    public List<Book> renting() {
        /*
            A method that retuns a List of currently rented books by the member.
                Parameters --> None
                Returns --> None
        */

        return currRented;
    }

    public static List<Book> commonBooks(Member[] members) {    
        /*
            A method that retuns a List of commonly rented books by a certain number of members.
                Parameters --> Member[] members
                Returns --> List of common books.
        */

        if (members == null) {
            return null;
        } 

        if (Arrays.asList(members).contains(null)) {
            return null;
        } 

        Map<Book,Integer> check = new HashMap<>(); //associate a book with a certain number copies of itself.
        Set<Book> union = new HashSet<>();

        for (Member member: members) {
            Set<Book> memberRentedHist = new HashSet<>(member.history()); //removing duplicates from history
            for (Book book: memberRentedHist) {
                check.putIfAbsent(book, 0);
                check.computeIfPresent(book, (k, v) -> v+1);
            }
        }
        
        for (Map.Entry<Book,Integer> entry: check.entrySet()) {
            
            //an intersection of books amongst X number of members is when the occurrence of a book == the number of members.
            if (entry.getValue() == members.length) {
                union.add(entry.getKey()); 
            } 
        }
        
        return sortBooks(new ArrayList<>(union));
    }
    
}
