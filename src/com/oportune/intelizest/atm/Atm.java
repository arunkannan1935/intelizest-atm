package com.oportune.intelizest.atm;

import java.util.*;

public class Atm {
  private static final List<Integer> currencies = new ArrayList();
  private static final Map<Integer, Integer> availableCurrency = new HashMap();
  private static int balance = 0;
  private static final Scanner scanner = new Scanner(System.in);

  public static void main(String[] args) {
    initializeAtm();

    while (true) {
      System.out.println("*** Welcome to Oportune ATM machine ***");
      System.out.println("Please select your option to proceed");
      System.out.print("\t 1. Deposit \n \t 2. Withdraw \n \t 3. Balance \n: ");
      int option = scanner.nextInt();
      if (option == 1) {
        deposit();
      } else if (option == 2) {
        withdraw();
      } else if (option == 3) {
        printBalance();
      } else {
        System.out.println("Please enter valid option");
      }
    }
  }

  private static void initializeAtm() {
    initializeCurrency();
    initializeCurrencyCalculator(currencies);
  }

  private static void initializeCurrencyCalculator(List<Integer> currencies) {
    for (Integer eachCurrency : currencies) {
      availableCurrency.put(eachCurrency, 0);
    }
  }

  private static void initializeCurrency() {
    currencies.add(20);
    currencies.add(10);
    currencies.add(5);
    currencies.add(1);
  }

  private static void withdraw() {
    System.out.println("Welcome to withdraw screen");
    System.out.print("Please enter amount to withdraw: ");
    int amountToWithdraw = scanner.nextInt();
    if (validWithdrawalAmount(amountToWithdraw)) {
      withdrawAndReconcile(amountToWithdraw);
      System.out.println("Please collect the money and card");
      printBalance();
    } else {
      System.out.println("Please enter the valid amount");
    }
  }

  private static void withdrawAndReconcile(int amountToWithdraw) {
    currencyReconcile(amountToWithdraw);
    balanceReconcile(amountToWithdraw);
  }

  private static void balanceReconcile(int amountToWithdraw) {
    balance = balance - amountToWithdraw;
  }

  private static void currencyReconcile(int amountToWithdraw) {
    int remaining = amountToWithdraw;
    for (int currency : currencies) {
      if (availableCurrency.get(currency) > 0 && remaining > 0) {
        int currencyCount = remaining / currency;
        if (availableCurrency.get(currency) - currencyCount < 0) {
          remaining = remaining - (currency * availableCurrency.get(currency));
          availableCurrency.put(currency, 0);
        } else {
          remaining = remaining - (currency * currencyCount);
          availableCurrency.put(currency, availableCurrency.get(currency) - currencyCount);
        }
      }
    }
  }

  private static boolean validWithdrawalAmount(int amountToWithdraw) {
    return amountToWithdraw > 0
        && amountToWithdraw <= balance
        && validateAgainstCurrency(amountToWithdraw);
  }

  private static boolean validateAgainstCurrency(int amountToWithdraw) {
    int remaining = amountToWithdraw;
    Map<Integer, Integer> tempCurrency = availableCurrency;
    for (int currency : currencies) {
      if (tempCurrency.get(currency) > 0 && remaining > 0) {
        int currencyCount = remaining / currency;
        if (tempCurrency.get(currency) - currencyCount < 0)
          remaining = remaining - (currency * tempCurrency.get(currency));
        else remaining = remaining - (currency * currencyCount);
      }
    }
    return (remaining == 0);
  }

  private static void deposit() {
    System.out.println("Welcome to deposit screen");
    System.out.print("Please enter the amount you would like to deposit: ");
    int amountToDeposit = scanner.nextInt();
    if (validAmountToDeposit(amountToDeposit)) {
      Map<Integer, Integer> userTempCurrencies = getCurrencyDetails();
      boolean isValid = validateCurrencies(amountToDeposit, userTempCurrencies);
      if (isValid) {
        updateAvailableBalance(amountToDeposit);
        updateAvailableCurrency(userTempCurrencies);
        System.out.println("Amount deposited successfully");
        printBalance();
      }
    }
  }

  private static Map<Integer, Integer> getCurrencyDetails() {
    Map<Integer, Integer> userTempCurrencies = new HashMap<>();
    for (int currency : currencies) {
      System.out.print("Please enter number of $" + currency + "'s: ");
      userTempCurrencies.put(currency, scanner.nextInt());
    }
    return userTempCurrencies;
  }

  private static void printBalance() {
    System.out.println("Total Balance in ATM: " + balance);
    for (Map.Entry<Integer, Integer> currency : availableCurrency.entrySet()) {
      System.out.println("Available $" + currency.getKey() + "'s: " + currency.getValue());
    }
  }

  private static void updateAvailableCurrency(Map<Integer, Integer> userTempCurrencies) {
    for (Map.Entry<Integer, Integer> currency : userTempCurrencies.entrySet()) {
      availableCurrency.put(
          currency.getKey(), currency.getValue() + availableCurrency.get(currency.getKey()));
    }
  }

  private static void updateAvailableBalance(int amountToDeposit) {
    balance = balance + amountToDeposit;
  }

  private static boolean validateCurrencies(
      int amountToDeposit, Map<Integer, Integer> userTempCurrencies) {
    if (validateUserCurrenciesNegative(userTempCurrencies)) {
      System.out.println("Incorrect currency");
      return false;
    }
    if (!validateCurrencyAgainstTotal(amountToDeposit, userTempCurrencies)) {
      System.out.println("Total amount is not matching with currency entered");
      return false;
    }
    return true;
  }

  private static boolean validAmountToDeposit(int amountToDeposit) {
    if (validateDepositAmountNegative(amountToDeposit)) {
      System.out.println("Incorrect deposit amount");
      return false;
    }
    if (validateDepositAmountZero(amountToDeposit)) {
      System.out.println("Deposit amount cannot be zero");
      return false;
    }
    return true;
  }

  private static boolean validateUserCurrenciesNegative(Map<Integer, Integer> userTempCurrencies) {
    for (Map.Entry<Integer, Integer> currency : userTempCurrencies.entrySet()) {
      if (currency.getValue() < 0) return true;
    }
    return false;
  }

  private static boolean validateDepositAmountZero(int amountToDeposit) {
    return amountToDeposit == 0;
  }

  private static boolean validateDepositAmountNegative(int amountToDeposit) {
    return (amountToDeposit < 0);
  }

  private static boolean validateCurrencyAgainstTotal(
      int amountToDeposit, Map<Integer, Integer> userTempCurrencies) {
    int total = 0;
    for (Map.Entry<Integer, Integer> currency : userTempCurrencies.entrySet()) {
      total = total + (currency.getKey() * currency.getValue());
    }
    return (total == amountToDeposit);
  }
}
