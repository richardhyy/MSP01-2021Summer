package cc.eumc.expbank.model;

import java.io.*;

public class PlayerAccount implements Serializable {
    private int balance;
    private int maxBalance = 0;

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        if (balance > this.maxBalance) {
            this.maxBalance = balance;
        }

        this.balance = balance;
    }

    public int getMaxBalance() {
        return maxBalance;
    }

    public void save(File destinationFile) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(destinationFile);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(this);
        out.close();
        fileOut.close();
    }

    public static PlayerAccount load(File sourceFile) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(sourceFile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        PlayerAccount playerAccount = (PlayerAccount) in.readObject();
        in.close();
        fileIn.close();
        return playerAccount;
    }
}
