package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.AccountNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.FolderNotFoundException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.InvalidPathException;
import bg.sofia.uni.fmi.mjt.mail.exceptions.RuleAlreadyDefinedException;
import bg.sofia.uni.fmi.mjt.mail.services.StringService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static bg.sofia.uni.fmi.mjt.mail.constants.Constants.*;

public class Outlook implements MailClient {
    private Set<Account> accounts;
    private Map<Account, Map<String, List<Mail>>> folders;
    private Map<Account, List<Rule>> accountRules;
    private List<Rule> allRules;

    public Outlook() {
        this.accounts = new HashSet<>();
        this.folders = new HashMap<>();
        this.accountRules = new HashMap<>();
    }

    @Override
    public Account addNewAccount(String accountName, String email) {
        if (!StringService.areAllStringsValid(accountName, email)) {
            throw new IllegalArgumentException("Account name and email should be a valid strings");
        }

        if (this.getAccountByUsername(accountName).isPresent() || this.getAccountByEmail(email).isPresent()) {
            throw new AccountAlreadyExistsException();
        }

        Account newAccount = new Account(email, accountName);
        accounts.add(newAccount);
        this.folders.put(newAccount, new HashMap<>());
        this.createFolder(accountName, "/sent");
        this.createFolder(accountName, "/inbox");

        return newAccount;
    }

    @Override
    public void createFolder(String accountName, String path) {
        if (!StringService.areAllStringsValid(accountName, path)) {
            throw new IllegalArgumentException();
        }

        Optional<Account> accountFound = this.getAccountByUsername(accountName);

        if (accountFound.isEmpty()) {
            throw new AccountNotFoundException();
        }

        Account account = accountFound.get();

        if (Character.compare(path.charAt(0), PATH_SEPARATOR) != 0 && !path.startsWith(RECEIVED_MAILS_PATH)) {
            throw new InvalidPathException();
        }

        if (this.isFolderAlreadyCreated(path, account)) {
            throw new FolderAlreadyExistsException();
        }

        if (this.doesFolderPathExist(path, account)) {
            throw new InvalidPathException();
        }

        folders.get(account).put(path, new LinkedList<>());
    }

    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        if (!StringService.isValidString(accountName)) {
            throw new IllegalArgumentException();
        }

        Optional<Account> accountFound = this.getAccountByUsername(accountName);

        if (accountFound.isEmpty()) {
            throw new AccountNotFoundException();
        }

        Account account = accountFound.get();

        if (!this.isFolderAlreadyCreated(folderPath, account)) {
            throw new FolderNotFoundException();
        }

        int countOfDefinitions = folderPath.split("subject-includes:").length - 1;
        countOfDefinitions += folderPath.split("subject-or-body-includes:").length - 1;
        countOfDefinitions += folderPath.split("recipients-includes:").length - 1;
        countOfDefinitions += folderPath.split("from:").length - 1;

        if (countOfDefinitions != 4) {
            throw new RuleAlreadyDefinedException();
        }

        Rule rule = new Rule(folderPath, ruleDefinition, priority);

        if (!this.hasConflictRule(rule)) {
            this.allRules.add(rule);
            this.accountRules.get(account).add(rule);
            this.executeRule(rule, account);
        }
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        if (!StringService.areAllStringsValid(accountName, mailMetadata, mailContent)) {
            throw new IllegalArgumentException();
        }

        Optional<Account> accountFound = this.getAccountByUsername(accountName);

        if (accountFound.isEmpty()) {
            throw new AccountNotFoundException();
        }

        Account account = accountFound.get();

        String[] metadata = mailMetadata.split(System.lineSeparator());
        String sender = metadata[0].split(": ")[1].strip();
        String subject = metadata[1].split(": ")[1].strip();
        Set<String> recipients = new HashSet<>(Arrays.asList(metadata[2].split(": ")[1].split(", ")));

        for (String s : recipients) { //Stream API is not allowed
            s = s.strip();
        }

        String receivedDate = metadata[2].split(": ")[1];
        LocalDateTime date = LocalDateTime.parse(receivedDate);

        Mail mail = new Mail(account, recipients, subject, mailContent, date);

        this.folders.get(account).get(RECEIVED_MAILS_PATH)
            .add(mail);
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        if (!StringService.areAllStringsValid(account, folderPath)) {
            throw new IllegalArgumentException();
        }

        Optional<Account> accountFound = this.getAccountByUsername(account);
        if (accountFound.isEmpty()) {
            throw new AccountNotFoundException();
        }

        Account acc = accountFound.get();

        if (!this.folders.get(acc).containsKey(folderPath)) {
            throw new FolderNotFoundException();
        }

        return this.folders.get(acc).get(folderPath);
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        if (StringService.areAllStringsValid(accountName, mailMetadata, mailContent)) {
            throw new IllegalArgumentException();
        }
        Optional<Account> accountFound = this.getAccountByUsername(accountName);

        if (accountFound.isEmpty()) {
            throw new AccountNotFoundException();
        }

        String finalMailMetadata = mailMetadata;

        if (!finalMailMetadata.contains("sender: ")) {
            finalMailMetadata = "sender: " + accountFound.get().emailAddress() + System.lineSeparator();
        }

        String[] metadata = finalMailMetadata.split(System.lineSeparator());
        String sender = metadata[0].split(": ")[1].strip();
        String subject = metadata[1].split(": ")[1].strip();
        Set<String> recipients = new HashSet<>(Arrays.asList(metadata[2].split(": ")[1].split(", ")));

        for (String s : recipients) { //Stream API is not allowed
            s = s.strip();
            Optional<Account> receiverFound = this.getAccountByEmail(s);
            if (receiverFound.isPresent()) {
                this.receiveMail(accountFound.get().name(), finalMailMetadata, mailContent);
            }
        }

        String receivedDate = metadata[2].split(": ")[1];
        LocalDateTime date = LocalDateTime.parse(receivedDate);

        this.folders.get(accountFound.get()).get(SENT_MAILS_PATH)
            .add(new Mail(accountFound.get(), recipients, subject, mailContent, date));
    }

    private Optional<Account> getAccountByUsername(String username) {
        Account account = null;
        for (Account a : this.accounts) {
            if (a.name().equals(username)) {
                account = a;
                break;
            }
        }

        return Optional.ofNullable(account);
    }

    private Optional<Account> getAccountByEmail(String email) {
        Account account = null;
        for (Account a : this.accounts) {
            if (a.emailAddress().equals(email)) {
                account = a;
                break;
            }
        }

        return Optional.ofNullable(account);
    }

    private boolean isFolderAlreadyCreated(String path, Account account) {
        return this.folders.get(account).keySet().contains(path);
    }

    private boolean doesFolderPathExist(String path, Account account) {
        String pathWithoutFolderToBeCreated = path.substring(0, path.lastIndexOf(PATH_SEPARATOR));

        return this.folders.get(account).containsKey(pathWithoutFolderToBeCreated);
    }

    private boolean hasConflictRule(Rule rule) {
        for (Rule r : this.allRules) {
            if (r.priority() == rule.priority()
                && r.definition().equals(rule.definition())
                && !r.folderPath().equals(rule.folderPath())) {
                return true;
            }
        }

        return false;
    }

    private void executeRule(Rule rule, Account account) {
        List<Mail> mails = this.folders.get(account).get("\\inbox");

        for (Mail mail : mails) {
            if (this.doesRuleFit(rule, mail)) {
                this.folders.get(account).get(rule.folderPath()).add(mail);
            }
        }
    }

    private boolean doesRuleFit(Rule rule, Mail mail) {
        List<String> subjectIncludes = new ArrayList<>();
        List<String> subjectOrBodyIncludes = new ArrayList<>();
        List<String> recipientsIncludes = new ArrayList<>();
        String sender = "";

        String[] data = rule.definition().split(System.lineSeparator());

        for (String line : data) {
            if (line.contains("subject-includes")) {
                subjectIncludes = this.getKeywords(line);
            }
            if (line.contains("subject-or-body-includes")) {
                subjectOrBodyIncludes = this.getKeywords(line);
            }
            if (line.contains("recipients-includes")) {
                recipientsIncludes = this.getRecipients(line);
            }
            if (line.contains("from")) {
                sender = this.getSenderMail(line);
            }
        }

        if (!mail.sender().emailAddress().equals(sender)) {
            return false;
        }

        String subjectAndBody = mail.subject();
        subjectAndBody += " ";
        subjectAndBody += mail.body();

        for (String s : subjectOrBodyIncludes) {
            if (!subjectAndBody.contains(s)) {
                return false;
            }
        }

        for (String s : subjectIncludes) {
            if (!mail.subject().contains(s)) {
                return false;
            }
        }

        for (String s : recipientsIncludes) {
            if (mail.recipients().contains(s)) {
                return true;
            }
        }

        return false;
    }

    private List<String> getKeywords(String line) {
        return List.of(line.split(": ")[1].strip().split(", "));
    }

    private String getSenderMail(String line) {
        return line.split(": ")[2];
    }

    private List<String> getRecipients(String line) {
        return List.of(line.split(": ")[1].strip().split(", "));
    }
}
