package oop.ex6.main;

import java.util.List;

public class Sjavac {

    private static final int FILE_PATH = 0;
    private static final int ARGS_LENGTH = 1;
    private static final int IO_ERROR_NUM = 2;
    private static final int SUCCESS_NUM = 0;
    private static final int FAIL_NUM = 1;
    private static final String WRONG_NUMBER_OF_ARGS = "Wrong number of arguments.";


    public static void main(String[] args){

        //checking there is only one argument given.
        if (args.length != ARGS_LENGTH){
            printIOException(WRONG_NUMBER_OF_ARGS);
            return;
        }

        // reading the file.
        List<String> lines;
        try{
            lines = FileReader.readFile(args[FILE_PATH]);
        } catch (Exception c){
            printIOException(c.getMessage());
            return;
        }

        // checking the file's content.
        try {
            new Scope(null, lines);
	        System.out.println(SUCCESS_NUM);
        }catch(Exception c) {
	        System.out.println(FAIL_NUM);
        }
    }

    /**
     * printing the exception's message in case of an IO exception.
     * @param exception_message the message of the exception thrown.
     */
    private static void printIOException(String exception_message) {
        System.out.print(IO_ERROR_NUM);
        System.err.println(exception_message);
    }
}
