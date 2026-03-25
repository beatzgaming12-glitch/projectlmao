module org.example.player {
    requires javafx.controls;
    requires javafx.media;
    requires java.desktop;
    requires java.net.http;
    requires java.prefs;

    exports org.example;
    opens org.example to javafx.graphics;
}
