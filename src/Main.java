import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Welcome to the mock up access control system for Open Bet!");
        System.out.println("**********************************************************");
        AccessControl test = new AccessControl();
        String menu;
        do {
            System.out.println("Type 1 to register a new user, type 2 for returning user log in. " +
                    "Type \"Exit\" to quit mockup. ");
            Scanner sc = new Scanner(System.in);
            menu = sc.nextLine();
            switch (menu) {
                case "1":
                    if (test.userRegister()) {
                        System.out.println("**********************************************************");
                        System.out.println("Account successfully created! Now try to log in!");
                    }
                break;

                case "2":
                    if (test.loginVerify()) {
                        System.out.println("**********************************************************");
                        System.out.println("Account successfully logged in! Now imagine yourself start playing!");
                        while (test.verifySession()) {
                            System.out.println("**********************************************************");
                        }
                    }
                break;
                case "Exit":
                    System.out.println("Bye! Hope you like our mock up system!");
                break;

                default:
                    System.out.println("Unrecognized input, please try again!");
            }
        } while (!menu.equals("Exit"));
    }
}
