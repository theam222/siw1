import java.util.Objects;

public class Comment {
    private String author;
    private String text;

    Comment(String author, String text) {
        this.author = "Anonymous";
        this.text = "";
        setAuthor(author);
        setText(text);
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public void setAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            System.out.println("!! Rejected comment author");
            return;
        }
        this.author = author.trim();
    }

    public void setText(String text) {
        if (text == null || text.trim().length() < 2) {
            System.out.println("!! Rejected comment text");
            return;
        }
        this.text = text.trim();
    }

    public boolean isValid() {
        return !author.equals("Anonymous") && text.length() >= 2;
    }

    public void display() {
        System.out.println("   - " + author + ": " + text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment other = (Comment) o;
        return author.equals(other.author) && text.equals(other.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, text);
    }
}
