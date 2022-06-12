import java.time.Duration;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.function.Predicate;

public class AccessControl {

    /** Enumeration that represents the status of each users*/
    private enum Status {
        LOGIN, LOGOUT, ACTIVE, VERIFY, SUSPENDED
    }

    public AccessControl () {
        this.currentPlayer = null;
        this.passwords = new Hashtable<>();
        this.userStatus = new Hashtable<>();
        this.userData = new Hashtable<>();
    }

    public boolean userRegister() {
        System.out.println("Please type in the username and press enter:");
        Scanner sc = new Scanner(System.in);
        String user = sc.nextLine();
        System.out.println("Please type in the password and press enter:");
        String password = sc.nextLine();
        if (user == null || password == null) {
            System.out.println("Username or password is invalid. Please try again.");
            return false;
        }
        if (passwords.containsKey(user)) {
            System.out.println("This username has already been used! " +
                    "Choose another username or try login. ");
        } else {
            passwords.put(user, password);
            userStatus.put(user, Status.LOGOUT);
            userData.put(user, new MetaData());
            return true;
        }
        return false;
    }

    public boolean loginVerify() {
        System.out.println("Please type in the username and press enter:");
        Scanner sc = new Scanner(System.in);
        String user = sc.nextLine();
        System.out.println("Please type in the password and press enter:");
        String password = sc.nextLine();
        if (user == null || password == null) {
            System.out.println("Username or password is invalid. Please try again.");
            return false;
        }
        if (password.equals(passwords.get(user))) {
            MetaData curUser = userData.get(user);
            if (twoFactorAuthentication()) {
                userStatus.replace(user, Status.LOGIN);
                if (!curUser.isInitialized()) {
                    setUserMetaData(curUser);
                    userStatus.replace(user, Status.ACTIVE);
                    currentPlayer = user;
                    return true;
                } else if (curUser.isInitialized() && isSuspended(user)) {
                    displaySuspendedUser(curUser);
                    return false;
                } else {
                    displayUserMetaData(curUser);
                    System.out.println("Would you like to change your setting?");
                    String ch = sc.nextLine();
                    if (ch.charAt(0) == 'y' || ch.charAt(0) == 'Y') {
                        setUserMetaData(curUser);
                    } else {
                        curUser.playTimeCountDownRestart();
                    }
                    userStatus.replace(user, Status.ACTIVE);
                    currentPlayer = user;
                    return true;
                }
            } else {
                suspendAccount(user);
                curUser.suspendNoExclusion();
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean twoFactorAuthentication () {
        Scanner sc = new Scanner(System.in);
        System.out.println("This is a mock-up system, would you like the user " +
                "to pass two-factor authentication?(y/n)");
        System.out.println("Please note that a negative answer will result in " +
                "user account being suspended");
        String input = sc.next();
        if (input.charAt(0) == 'y' || input.charAt(0) == 'Y') {
            System.out.println("Congratulations! You passed the two-factor authentication!");
            return true;
        } else if (input.charAt(0) == 'n' || input.charAt(0) == 'N') {
            System.out.println("Oops! It seems your account is at risk. " +
                    "We will temporarily suspend it. Please contact a customer service operator.");
            return false;
        } else {
            System.out.println("Illegal inputs! Please try again.");
            return twoFactorAuthentication();
        }
    }

    public void setUserMetaData(MetaData curUser) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Setting a limit is a great way to reduce financial risk and improve " +
                "your experience at this website. \nPlease enter your deposit amount and bank information");
        int deposit = (int) (sc.nextFloat() * 100);
        System.out.println("Please enter the maximum amount of spending you are able to afford." +
                "\nPlease notice this is a hard limit and cannot be modified once you start playing");
        int maxSpend = (int) (sc.nextFloat() * 100);
        System.out.println("Please enter the maximum amount of loss you are able to afford." +
                "\nPlease notice this is a hard limit and cannot be modified once you start playing");
        int maxLoss = (int) (sc.nextFloat() * 100);
        System.out.println("Please enter the maximum number of hour you prepared to spend on this website."
                + "\nPlease notice the maximum option is 5 hours.");
        int playLimit = sc.nextInt();
        System.out.println("Please enter the minimum number of hour you prepared to log off this website " +
                "once you finished playing. \nPlease notice that the minimum option is 24 hours.");
        int exclusionTime = sc.nextInt();
        curUser.initializeMetaData(deposit, maxSpend, maxLoss, playLimit, exclusionTime);
    }

    public void displayUserMetaData(MetaData curUser) {
        System.out.println("You have " + curUser.getDeposit() + "$ of deposit left." );
        System.out.println("Your maximum spending goal is " + curUser.getMaxSpend() + "$");
        System.out.println("Your maximum loss cap is " + curUser.getMaxLoss() + "$");
        System.out.println("You will be asked to log off after " + curUser.getPlayTime());
        System.out.println("If you displays risky behaviour, you will be excluded from our service for "
                + curUser.getExclusionTime());
    }

    public void displaySuspendedUser(MetaData curUser) {
        System.out.println("Sorry, but your account is currently suspended.");
        Duration curUserExclusion = curUser.getExclusionTime();
        if (curUserExclusion.equals(Duration.ZERO)) {
            System.out.println("It possible someone compromise your password. " +
                    "Please contact customer service immediately to prevent financial loss.");
        } else if (curUserExclusion.equals(Duration.ofHours(Long.MAX_VALUE))) {
            System.out.println("Due to multiple violations of terms of service, " +
                    "your account has been locked. Please contact customer service tp unlock it.");
        }  else {
            System.out.println("Due to violation of terms of service, you account is suspended for"
                    + curUserExclusion.toString());
            System.out.println("It will be unlocked at " + curUser.unfreezeTime().toString() );
        }
    }

    public boolean isSuspended(String user) {
        if (currentPlayer.equals(user)) {
            currentPlayer = null;
        }
        return Status.SUSPENDED.equals(userStatus.get(user));
    }

    public void suspendAccount(String user) {
        if (userStatus.containsKey(user)) {
            userStatus.replace(user, Status.SUSPENDED);
        }
    }

    public boolean verifySession() {
        String user = currentPlayer;
        if (user == null) {
            System.out.println("Error! There is no active player in the mock up system!");
            return false;
        }

        Status st = userStatus.get(user);
        MetaData curUser = userData.get(user);
        if (st.equals(Status.SUSPENDED)) {
            displaySuspendedUser(curUser);
        } else {
            userStatus.replace(user, Status.VERIFY);
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter how much hour you have spend on this website without a break.");
        int playHourCur = sc.nextInt();
        System.out.println("Please enter how much money you have spend on the games. ");
        int spend = (int) (sc.nextFloat() * 100);
        System.out.println("Please enter how much money you have lose in total. ");
        int loss = (int) (sc.nextFloat() * 100);

        curUser.reduceDeposit(spend);
        curUser.setBeginTime(playHourCur);

        if (curUser.sessionTimeout() <= 0 &&
                curUser.getActualPlayTime().compareTo(Duration.ofHours(curUser.DEFAULT_MAXIMUM_PLAY_TIME)) >= 0) {
            System.out.println("You have spend more tha 5 hours at our website playing without any break. ");
            System.out.println("You will now be forced to log out of our website and will not be able to log in until "
                    + curUser.unfreezeTime());
             suspendAccount(user);
             curUser.suspendWithExclusion();
             return false;
        } else if (curUser.getDeposit() <= 0 || curUser.getMaxSpend() <=0 || curUser.getMaxLoss() <= 0
                || curUser.getMaxSpend() * 100 < spend || curUser.getMaxLoss() * 100 < loss) {
             System.out.println("You have breached the financial limits by spending all your deposits, " +
                    "spending or losing more that your set limit");
             System.out.println("\"You will now be forced to log out of our website and will not be able to log in until \"\n" +
                    "            + curUser.unfreezeTime()");
             curUser.suspendWithExclusion();
             return false;
        } else if (curUser.sessionTimeout() <= 0) {
            System.out.println("You have reached your personal play time limit on our website. ");
            displayUserMetaData(curUser);
            System.out.println("Would you like too continue playing?(y/n) " +
                    "\nIf you choose yes, your maximum loss and spending goal will be adjusted accordingly");
            String ch = sc.next();
            if (ch.charAt(0) == 'y' || ch.charAt(0) == 'Y') {
                curUser.reduceSpend(spend);
                curUser.reduceSpend(loss);
                userStatus.replace(user, Status.ACTIVE);
                displayUserMetaData(curUser);
                return true;
            } else {
                userStatus.replace(user, Status.LOGOUT);
                return false;
            }
        } else {
            System.out.println("Our system design will run an Machine Learning based anomaly detection program at this point. " +
                    "\n Failure will also lead to account suspension.");
            System.out.println("Would you like the anomaly detection success for this mock up?(y/n)");
            String ch = sc.next();
            if (ch.charAt(0) == 'y' || ch.charAt(0) == 'Y') {
                userStatus.replace(user, Status.ACTIVE);
                return true;
            } else {
                userStatus.replace(user, Status.SUSPENDED);
                System.out.println("Our system noticed something strange is going on with your account. \n" +
                        "Therefore, you will be temporarily suspended.");
                curUser.suspendWithExclusion();
                return false;
            }
        }

    }

    private String currentPlayer;

    /** Mock up for a database which contains all the user, password hash pairs.
      * It is generally a good practice to store password hash instead of password
      * in case the database gets compromised. Since this is just a mock up project,
      * we use password instead. */
    private Hashtable<String, String> passwords;

    /** Mock up for a server which keeps track of all the users sessions. */
    private Hashtable<String, Status> userStatus;

    /** Mock up for a database which stores all the user activities. */
    private Hashtable <String, MetaData> userData;
}
