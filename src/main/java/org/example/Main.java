package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.scene.shape.SVGPath;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.prefs.Preferences;

public class Main {
    public static void main(String[] args) {
        Application.launch(PlayerApp.class, args);
    }

    public static class PlayerApp extends Application {
        private static final String PREF_PLAYLIST = "savedPlaylist";
        private static final String PREF_VOLUME = "savedVolume";
        private static final String PREF_ACCENT = "savedAccentProjectLMAO";
        private static final String PREF_WINDOW_X = "windowX";
        private static final String PREF_WINDOW_Y = "windowY";
        private static final String PREF_WINDOW_W = "windowWidth";
        private static final String PREF_WINDOW_H = "windowHeight";
        private static final String PREF_WINDOW_MAXIMIZED = "windowCustomMaximized";
        private static final String PREF_RADIUS = "uiRadius";
        private static final String PREF_HERO_WIDTH = "heroWidth";
        private static final String PREF_ANIMATIONS = "animationsEnabled";
        private final ObservableList<File> playlist = FXCollections.observableArrayList();
        private final FilteredList<File> filteredPlaylist = new FilteredList<>(playlist, file -> true);
        private final ListView<File> playlistView = new ListView<>(filteredPlaylist);
        private final Preferences preferences = Preferences.userNodeForPackage(PlayerApp.class);

        private MediaPlayer mediaPlayer;
        private boolean loopEnabled = false;
        private boolean playlistRepeatEnabled = false;
        private boolean shuffleEnabled = false;
        private boolean muted = false;
        private int currentIndex = -1;
        private Color accentColor;
        private double lastVolumeBeforeMute = 65;
        private final Random random = new Random();

        private final Label sectionLabel = new Label("PLAYLIST LOCKER");
        private final Label appTitleLabel = new Label("Project L.M.A.O.");
        private final Label statusLabel = new Label("Drop tracks. Hit play. Stay loud.");
        private final Label nowPlayingCaption = new Label("NOW PLAYING");
        private final Label nowPlayingLabel = new Label("Nothing selected");
        private final Label songCountLabel = new Label("0 tracks loaded");
        private final Label elapsedTimeLabel = new Label("00:00");
        private final Label totalTimeLabel = new Label("00:00");
        private final Label volumeValueLabel = new Label("65%");
        private final Label footerSongLabel = new Label("Nothing selected");
        private final Label footerMetaLabel = new Label("Local build");
        private final Label footerArtLabel = new Label("\u266B");
        private final Label windowIconLabel = new Label();
        private final Label windowTitleLabel = new Label("Project L.M.A.O.");
        private final Label searchLabel = new Label("QUICK FIND");
        private final Label accentLabel = new Label("ACCENT");
        private final Label radiusLabel = new Label("CARD ROUNDING");
        private final Label heroWidthLabel = new Label("HERO WIDTH");
        private final Button playButton = new Button();
        private final Button previousButton = new Button("<<");
        private final Button nextButton = new Button(">>");
        private final Button loopButton = new Button();
        private final Button repeatButton = new Button();
        private final Button shuffleButton = new Button();
        private final Button muteButton = new Button();
        private final Button addSongButton = new Button("Add Song");
        private final Button addFolderButton = new Button("Add Folder");
        private final Button removeSongButton = new Button("\u232B");
        private final Button clearPlaylistButton = new Button("\uD83D\uDDD1");
        private final Button undoButton = new Button("Undo");
        private final Button minimizeWindowButton = new Button("\u2212");
        private final Button maximizeWindowButton = new Button("\u25A1");
        private final Button closeWindowButton = new Button("\u2715");
        private final Slider progressSlider = new Slider(0, 100, 0);
        private final Slider volumeSlider = new Slider(0, 100, 65);
        private final Slider cornerRadiusSlider = new Slider(8, 28, 16);
        private final Slider heroWidthSlider = new Slider(260, 420, 320);
        private final TextField searchField = new TextField();
        private final ColorPicker colorPicker = new ColorPicker();
        private final CheckBox animationsCheckBox = new CheckBox("Enable motion");
        private final Label colorPreview = new Label("LIVE PREVIEW");
        private final Label previewTitleLabel = new Label("Arena Card");
        private final Label previewBodyLabel = new Label("Selections, tabs, and transport controls will pulse with this accent.");
        private final Label previewChipLabel = new Label("Launch Action");
        private final Label colorHelpLabel = new Label("Pick a loud accent for the main UI glow, active tabs, and high-priority buttons.");
        private final Region colorSwatch = new Region();
        private final FlowPane colorPalette = new FlowPane(10, 10);
        private final Label progressHoverLabel = new Label("00:00");
        private Stage attachedBarStage;
        private BorderPane root;
        private VBox bottomPanel;
        private VBox heroPanel;
        private VBox appearanceCard;
        private VBox behaviorCard;
        private HBox titleBar;
        private StackPane progressBarControl;
        private StackPane volumeBarControl;
        private final Region progressBarBase = new Region();
        private final Region progressBarFill = new Region();
        private final Region progressBarThumb = new Region();
        private final Region volumeBarBase = new Region();
        private final Region volumeBarFill = new Region();
        private final Region volumeBarThumb = new Region();
        private final SVGPath playButtonGlyph = new SVGPath();
        private final SVGPath windowIconGlyph = new SVGPath();
        private Label playlistBadge;
        private Label albumArt;
        private TabPane tabPane;
        private boolean isSeeking = false;
        private boolean resumeAfterSeek = false;
        private UndoAction lastUndoAction = UndoAction.none();
        private double dragOffsetX;
        private double dragOffsetY;
        private double restoredX;
        private double restoredY;
        private double restoredWidth;
        private double restoredHeight;
        private boolean customMaximized = false;
        private boolean resizing = false;
        private Cursor resizeCursor = Cursor.DEFAULT;
        private double resizeStartX;
        private double resizeStartY;
        private double resizeStartWindowX;
        private double resizeStartWindowY;
        private double resizeStartWidth;
        private double resizeStartHeight;

        private enum UndoKind {
            NONE,
            REMOVE,
            CLEAR
        }

        private record UndoAction(UndoKind kind, List<File> files, int insertIndex, int previousCurrentIndex) {
            private static UndoAction none() {
                return new UndoAction(UndoKind.NONE, List.of(), -1, -1);
            }
        }

        @Override
        public void start(Stage stage) {
            accentColor = Color.web(preferences.get(PREF_ACCENT, "#2b83bd"));
            cornerRadiusSlider.setValue(preferences.getDouble(PREF_RADIUS, 16));
            heroWidthSlider.setValue(preferences.getDouble(PREF_HERO_WIDTH, 320));
            animationsCheckBox.setSelected(preferences.getBoolean(PREF_ANIMATIONS, true));
            playlistView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            playlistView.setPlaceholder(createPlaceholder());
            playlistView.setCellFactory(list -> new ListCell<>() {
                private final Label title = new Label();
                private final Label order = new Label();
                private final Label icon = new Label("\u266B");
                private final VBox textBox = new VBox(2, title);
                private final HBox row = new HBox(12, order, icon, textBox);

                {
                    hoverProperty().addListener((obs, oldValue, newValue) -> {
                        if (!isEmpty()) {
                            updateCellAppearance();
                        }
                    });
                }

                @Override
                protected void updateItem(File item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                        setStyle(
                                "-fx-background-color: transparent; " +
                                "-fx-border-color: transparent; " +
                                "-fx-background-radius: 0; " +
                                "-fx-border-radius: 0; " +
                                "-fx-padding: 0;"
                        );
                        return;
                    }

                    title.setText(formatSongTitle(item));
                    order.setText(String.format(Locale.ROOT, "%02d", playlist.indexOf(item) + 1));
                    row.setAlignment(Pos.CENTER_LEFT);
                    setText(null);
                    setGraphic(row);
                    updateCellAppearance();
                }

                @Override
                public void updateSelected(boolean selected) {
                    super.updateSelected(selected);
                    if (!isEmpty()) {
                        updateCellAppearance();
                    }
                }

                private void updateCellAppearance() {
                    String accent = toHex(accentColor);
                    boolean selected = isSelected();
                    boolean hovered = isHover();
                    String rowFill = selected ? withOpacity(accentColor, 0.20) : (hovered ? withOpacity(accentColor, 0.10) : "transparent");
                    String rowBorder = selected ? withOpacity(accentColor, 0.50) : (hovered ? withOpacity(accentColor, 0.28) : "rgba(255,255,255,0.03)");

                    title.setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: " + (selected ? "700" : "600") + ";");
                    order.setStyle("-fx-text-fill: " + (selected ? accent : "#7f8791") + "; -fx-font-size: 11px; -fx-min-width: 30px; -fx-font-weight: 700;");
                    icon.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: " + ((selected || hovered) ? "18px" : "16px") + "; -fx-min-width: 28px; -fx-alignment: center;");
                    setStyle(
                            "-fx-background-color: " + rowFill + "; " +
                            "-fx-background-radius: 16; " +
                            "-fx-border-color: " + rowBorder + "; " +
                            "-fx-border-radius: 16; " +
                            "-fx-padding: 10 12 10 12;"
                    );
                }
            });

            addSongButton.setOnAction(event -> addSongs(stage));
            addFolderButton.setOnAction(event -> addFolder(stage));
            removeSongButton.setOnAction(event -> removeSelectedSong());
            clearPlaylistButton.setOnAction(event -> clearPlaylist());
            undoButton.setOnAction(event -> undoLastAction());

            playButton.setOnAction(event -> togglePlayback());
            previousButton.setOnAction(event -> playPreviousSong(true));
            nextButton.setOnAction(event -> playNextSong(true));
            loopButton.setOnAction(event -> toggleLoop());
            repeatButton.setOnAction(event -> togglePlaylistRepeat());
            shuffleButton.setOnAction(event -> toggleShuffle());
            muteButton.setOnAction(event -> toggleMute());
            colorPicker.setValue(accentColor);
            colorPicker.setOnAction(event -> {
                accentColor = colorPicker.getValue();
                preferences.put(PREF_ACCENT, toHex(accentColor));
                applyTheme();
                Platform.runLater(colorPicker::requestFocus);
            });
            populateColorPalette();
            cornerRadiusSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                preferences.putDouble(PREF_RADIUS, newValue.doubleValue());
                applyTheme();
            });
            heroWidthSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                preferences.putDouble(PREF_HERO_WIDTH, newValue.doubleValue());
                if (heroPanel != null) {
                    heroPanel.setPrefWidth(newValue.doubleValue());
                }
            });
            animationsCheckBox.selectedProperty().addListener((obs, oldValue, newValue) ->
                    preferences.putBoolean(PREF_ANIMATIONS, newValue));

            updatePlayButtonLabel();
            updateLoopButtonLabel();
            updateRepeatButtonLabel();
            updateShuffleButtonLabel();
            updateMuteButtonLabel();
            installTooltips();
            previousButton.setDisable(true);
            nextButton.setDisable(true);
            removeSongButton.setDisable(true);
            clearPlaylistButton.setDisable(true);
            undoButton.setDisable(true);
            configureSliders();
            configureSearch();
            configurePlayButtonGraphic();
            configureWindowIconGraphic();

            playlistView.setOnMouseClicked(event -> {
                if (event.getClickCount() != 1) {
                    return;
                }

                Node target = event.getPickResult().getIntersectedNode();
                while (target != null && !(target instanceof ListCell<?>)) {
                    target = target.getParent();
                }

                if (target instanceof ListCell<?> cell && !cell.isEmpty()) {
                    Object item = cell.getItem();
                    if (item instanceof File selectedFile) {
                        playSelectedSongIfNeeded(selectedFile);
                    }
                }
            });
            playlistView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
                removeSongButton.setDisable(newValue == null);
                if (newValue != null && mediaPlayer == null) {
                    statusLabel.setText("Selected: " + formatSongTitle(newValue));
                }
            });

            loadSavedPlaylist();

            heroPanel = createHeroPanel();
            VBox playlistPanel = createPlaylistPanel();
            bottomPanel = createBottomPanel();
            titleBar = createTitleBar(stage);

            root = new BorderPane();
            root.setTop(titleBar);
            root.setLeft(heroPanel);
            root.setCenter(playlistPanel);
            installDragAndDrop(root);

            Scene scene = new Scene(root, 980, 620);
            scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/app-ui.css")).toExternalForm());
            installResizeSupport(stage, scene);
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.DELETE) {
                    removeSelectedSong();
                } else if (event.getCode() == KeyCode.ENTER) {
                    togglePlayback();
                } else if (event.getCode() == KeyCode.SPACE && !searchField.isFocused()) {
                    togglePlayback();
                    event.consume();
                }
            });

            stage.setTitle("Project L.M.A.O.");
            stage.initStyle(StageStyle.UNDECORATED);
            stage.getIcons().setAll(
                    createAppIcon(16),
                    createAppIcon(24),
                    createAppIcon(32),
                    createAppIcon(48),
                    createAppIcon(64),
                    createAppIcon(128),
                    createAppIcon(256)
            );
            stage.setScene(scene);
            stage.setResizable(true);
            stage.setMinWidth(1080);
            stage.setMinHeight(460);
            attachedBarStage = createAttachedBarStage(stage);
            restoreWindowBounds(stage);
            stage.show();
            attachedBarStage.show();
            restoreMaximizedState(stage);
            updateAttachedBarBounds(stage);
            stage.xProperty().addListener((obs, oldValue, newValue) -> {
                if (!customMaximized) {
                    preferences.putDouble(PREF_WINDOW_X, newValue.doubleValue());
                }
                updateAttachedBarBounds(stage);
            });
            stage.yProperty().addListener((obs, oldValue, newValue) -> {
                if (!customMaximized) {
                    preferences.putDouble(PREF_WINDOW_Y, newValue.doubleValue());
                }
                updateAttachedBarBounds(stage);
            });
            stage.widthProperty().addListener((obs, oldValue, newValue) -> {
                if (!customMaximized) {
                    preferences.putDouble(PREF_WINDOW_W, newValue.doubleValue());
                }
                updateAttachedBarBounds(stage);
            });
            stage.heightProperty().addListener((obs, oldValue, newValue) -> {
                if (!customMaximized) {
                    preferences.putDouble(PREF_WINDOW_H, newValue.doubleValue());
                }
                updateAttachedBarBounds(stage);
            });
            stage.iconifiedProperty().addListener((obs, oldValue, iconified) -> {
                if (attachedBarStage == null) {
                    return;
                }
                if (iconified) {
                    attachedBarStage.hide();
                } else {
                    attachedBarStage.show();
                    updateAttachedBarBounds(stage);
                }
            });
            stage.setOnCloseRequest(event -> {
                if (attachedBarStage != null) {
                    attachedBarStage.close();
                }
            });

            applyTheme();
            Platform.runLater(this::updateCustomBars);
        }

        private void addSongs(Stage stage) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Audio Files");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav")
            );

            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(stage);
            if (selectedFiles == null || selectedFiles.isEmpty()) {
                return;
            }

            addFilesToPlaylist(selectedFiles);
        }

        private void playSelectedOrCurrentSong() {
            if (playlist.isEmpty()) {
                showError("Playlist is empty", "Add at least one .mp3 or .wav file.");
                return;
            }

            File selectedFile = playlistView.getSelectionModel().getSelectedItem();
            if (selectedFile != null) {
                playSongAt(playlist.indexOf(selectedFile));
                return;
            }

            if (currentIndex >= 0 && currentIndex < playlist.size()) {
                playSongAt(currentIndex);
                return;
            }

            playSongAt(0);
        }

        private void playSelectedSongIfNeeded(File selectedFile) {
            int selectedIndex = playlist.indexOf(selectedFile);
            if (selectedIndex < 0) {
                return;
            }

            if (selectedIndex == currentIndex && mediaPlayer != null) {
                MediaPlayer.Status status = mediaPlayer.getStatus();
                if (status == MediaPlayer.Status.PLAYING || status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY) {
                    return;
                }
            }

            playSongAt(selectedIndex);
        }

        private void playSongAt(int index) {
            if (index < 0 || index >= playlist.size()) {
                return;
            }

            File songFile = playlist.get(index);
            disposeCurrentPlayer();

            try {
                Media media = new Media(songFile.toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                currentIndex = index;
                animateTrackChange();

                if (filteredPlaylist.contains(songFile)) {
                    playlistView.getSelectionModel().select(songFile);
                    playlistView.scrollTo(songFile);
                }
                nowPlayingLabel.setText(formatSongTitle(songFile));
                updateFooterTrackInfo();
                statusLabel.setText("Playing from local files");
                applyVolume();
                updatePlayButtonLabel();
                previousButton.setDisable(playlist.isEmpty());
                nextButton.setDisable(playlist.isEmpty());
                progressSlider.setDisable(false);

                mediaPlayer.setOnPlaying(this::updatePlayButtonLabel);
                mediaPlayer.setOnPaused(this::updatePlayButtonLabel);
                mediaPlayer.setOnStopped(this::updatePlayButtonLabel);

                mediaPlayer.setOnEndOfMedia(() -> {
                    if (loopEnabled) {
                        mediaPlayer.seek(Duration.ZERO);
                        mediaPlayer.play();
                    } else {
                        playNextSong(false);
                    }
                });

                mediaPlayer.setOnReady(() -> {
                    Duration total = mediaPlayer.getTotalDuration();
                    totalTimeLabel.setText(formatDuration(total));
                    updateBarProgress();
                    mediaPlayer.play();
                });

                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> updateProgress(newTime));

                mediaPlayer.setOnError(() -> showError("Playback error", mediaPlayer.getError() != null
                        ? mediaPlayer.getError().getMessage()
                        : "Unknown media playback error."));
            } catch (MediaException ex) {
                showError("Unsupported media", "Could not play file: " + songFile.getName());
            }
        }

        private void playNextSong(boolean wrapAround) {
            int nextIndex = getNextIndex(wrapAround);
            if (nextIndex >= 0) {
                playSongAt(nextIndex);
                return;
            }

            stopPlayback();
            playlistView.getSelectionModel().clearSelection();
            nowPlayingLabel.setText("Playlist finished");
            statusLabel.setText("Queue completed");
            elapsedTimeLabel.setText("00:00");
            totalTimeLabel.setText("00:00");
            progressSlider.setValue(0);
            nextButton.setDisable(true);
        }

        private void playPreviousSong(boolean wrapAround) {
            int previousIndex;
            if (shuffleEnabled) {
                previousIndex = getRandomOtherIndex();
            } else if (currentIndex > 0) {
                previousIndex = currentIndex - 1;
            } else {
                previousIndex = wrapAround && !playlist.isEmpty() ? playlist.size() - 1 : -1;
            }
            if (previousIndex >= 0) {
                playSongAt(previousIndex);
            }
        }

        private void togglePlayback() {
            if (playlist.isEmpty()) {
                showError("Playlist is empty", "Add at least one .mp3 or .wav file.");
                return;
            }

            if (mediaPlayer == null) {
                playSelectedOrCurrentSong();
                return;
            }

            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                updatePlayButtonLabel();
                statusLabel.setText("Stopped at " + elapsedTimeLabel.getText());
            } else {
                mediaPlayer.play();
                updatePlayButtonLabel();
                statusLabel.setText("Playing from local files");
            }
        }

        private void stopPlayback() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            updatePlayButtonLabel();
            nowPlayingLabel.setText(currentIndex >= 0 && currentIndex < playlist.size()
                    ? formatSongTitle(playlist.get(currentIndex))
                    : "Nothing selected");
            updateFooterTrackInfo();
            statusLabel.setText(currentIndex >= 0 && currentIndex < playlist.size()
                    ? "Stopped"
                    : "Drop tracks. Hit play. Stay loud.");
            elapsedTimeLabel.setText("00:00");
            progressSlider.setValue(0);
            updateBarProgress();
        }

        private void toggleLoop() {
            loopEnabled = !loopEnabled;
            updateLoopButtonLabel();
            updateLoopButtonStyle();
        }

        private void togglePlaylistRepeat() {
            playlistRepeatEnabled = !playlistRepeatEnabled;
            updateRepeatButtonLabel();
            applyTheme();
        }

        private void toggleShuffle() {
            shuffleEnabled = !shuffleEnabled;
            updateShuffleButtonLabel();
            applyTheme();
        }

        private void toggleMute() {
            muted = !muted;
            if (muted) {
                lastVolumeBeforeMute = volumeSlider.getValue();
                volumeSlider.setValue(0);
            } else {
                volumeSlider.setValue(lastVolumeBeforeMute <= 0 ? 65 : lastVolumeBeforeMute);
            }
            updateMuteButtonLabel();
        }

        private void disposeCurrentPlayer() {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                mediaPlayer = null;
            }
        }

        private void showError(String title, String message) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Music Player");
            alert.setHeaderText(title);
            alert.setContentText(message);
            alert.showAndWait();
        }

        @Override
        public void stop() {
            savePlaylist();
            preferences.putDouble(PREF_VOLUME, volumeSlider.getValue());
            preferences.put(PREF_ACCENT, toHex(accentColor));
            disposeCurrentPlayer();
        }

        private VBox createHeroPanel() {
            playlistBadge = new Label("READY TO DROP");
            albumArt = new Label("\u266B");
            albumArt.setAlignment(Pos.CENTER);

            VBox heroPanel = new VBox(20, playlistBadge, statusLabel, albumArt, nowPlayingCaption, nowPlayingLabel, songCountLabel);
            heroPanel.setPadding(new Insets(30, 28, 30, 28));
            heroPanel.setPrefWidth(heroWidthSlider.getValue());
            return heroPanel;
        }

        private HBox createTitleBar(Stage stage) {
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox bar = new HBox(10, windowIconLabel, windowTitleLabel, spacer, minimizeWindowButton, maximizeWindowButton, closeWindowButton);
            bar.setAlignment(Pos.CENTER_LEFT);
            bar.setPadding(new Insets(10, 12, 10, 12));
            bar.setMaxWidth(Double.MAX_VALUE);

            minimizeWindowButton.setOnAction(event -> stage.setIconified(true));
            maximizeWindowButton.setOnAction(event -> toggleWindowMaximize(stage));
            closeWindowButton.setOnAction(event -> stage.close());

            bar.setOnMousePressed(event -> {
                dragOffsetX = event.getSceneX();
                dragOffsetY = event.getSceneY();
            });
            bar.setOnMouseDragged(event -> {
                if (customMaximized) {
                    return;
                }
                stage.setX(event.getScreenX() - dragOffsetX);
                stage.setY(event.getScreenY() - dragOffsetY);
            });
            bar.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    toggleWindowMaximize(stage);
                }
            });
            return bar;
        }

        private Stage createAttachedBarStage(Stage owner) {
            Stage barStage = new Stage();
            barStage.initOwner(owner);
            barStage.initStyle(StageStyle.UNDECORATED);
            barStage.setResizable(false);

            Scene barScene = new Scene(bottomPanel, owner.getWidth(), 108);
            barScene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource("/app-ui.css")).toExternalForm());
            barStage.setScene(barScene);
            return barStage;
        }

        private void updateAttachedBarBounds(Stage owner) {
            if (attachedBarStage == null) {
                return;
            }

            double barHeight = getAttachedBarHeight();
            attachedBarStage.setX(owner.getX());
            attachedBarStage.setY(owner.getY() + owner.getHeight());
            attachedBarStage.setWidth(owner.getWidth());
            attachedBarStage.setHeight(barHeight);
        }

        private double getAttachedBarHeight() {
            bottomPanel.applyCss();
            bottomPanel.layout();
            double preferred = bottomPanel.prefHeight(-1);
            return Math.max(92, preferred);
        }

        private void toggleWindowMaximize(Stage stage) {
            if (!customMaximized) {
                restoredX = stage.getX();
                restoredY = stage.getY();
                restoredWidth = stage.getWidth();
                restoredHeight = stage.getHeight();
                saveWindowBounds(restoredX, restoredY, restoredWidth, restoredHeight);
                var bounds = Screen.getPrimary().getVisualBounds();
                double barHeight = getAttachedBarHeight();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(Math.max(stage.getMinHeight(), bounds.getHeight() - barHeight));
                customMaximized = true;
                preferences.putBoolean(PREF_WINDOW_MAXIMIZED, true);
                maximizeWindowButton.setText("\u2750");
            } else {
                customMaximized = false;
                if (restoredWidth > 0 && restoredHeight > 0) {
                    stage.setX(restoredX);
                    stage.setY(restoredY);
                    stage.setWidth(restoredWidth);
                    stage.setHeight(restoredHeight);
                    saveWindowBounds(restoredX, restoredY, restoredWidth, restoredHeight);
                }
                preferences.putBoolean(PREF_WINDOW_MAXIMIZED, false);
                maximizeWindowButton.setText("\u25A1");
            }
            updateAttachedBarBounds(stage);
        }

        private void installResizeSupport(Stage stage, Scene scene) {
            final double border = 8;

            scene.setOnMouseMoved(event -> {
                if (customMaximized) {
                    scene.setCursor(Cursor.DEFAULT);
                    resizeCursor = Cursor.DEFAULT;
                    return;
                }

                double x = event.getSceneX();
                double y = event.getSceneY();
                double width = scene.getWidth();
                double height = scene.getHeight();

                boolean left = x <= border;
                boolean right = x >= width - border;
                boolean top = y <= border;
                boolean bottom = y >= height - border;

                if (left && top) {
                    resizeCursor = Cursor.NW_RESIZE;
                } else if (left && bottom) {
                    resizeCursor = Cursor.SW_RESIZE;
                } else if (right && top) {
                    resizeCursor = Cursor.NE_RESIZE;
                } else if (right && bottom) {
                    resizeCursor = Cursor.SE_RESIZE;
                } else if (left) {
                    resizeCursor = Cursor.W_RESIZE;
                } else if (right) {
                    resizeCursor = Cursor.E_RESIZE;
                } else if (top) {
                    resizeCursor = Cursor.N_RESIZE;
                } else if (bottom) {
                    resizeCursor = Cursor.S_RESIZE;
                } else {
                    resizeCursor = Cursor.DEFAULT;
                }

                scene.setCursor(resizeCursor);
            });

            scene.setOnMousePressed(event -> {
                resizing = resizeCursor != Cursor.DEFAULT;
                resizeStartX = event.getScreenX();
                resizeStartY = event.getScreenY();
                resizeStartWindowX = stage.getX();
                resizeStartWindowY = stage.getY();
                resizeStartWidth = stage.getWidth();
                resizeStartHeight = stage.getHeight();
            });

            scene.setOnMouseDragged(event -> {
                if (!resizing || customMaximized) {
                    return;
                }

                double deltaX = event.getScreenX() - resizeStartX;
                double deltaY = event.getScreenY() - resizeStartY;
                double minWidth = stage.getMinWidth();
                double minHeight = stage.getMinHeight();

                if (resizeCursor == Cursor.E_RESIZE || resizeCursor == Cursor.NE_RESIZE || resizeCursor == Cursor.SE_RESIZE) {
                    stage.setWidth(Math.max(minWidth, resizeStartWidth + deltaX));
                }
                if (resizeCursor == Cursor.S_RESIZE || resizeCursor == Cursor.SE_RESIZE || resizeCursor == Cursor.SW_RESIZE) {
                    stage.setHeight(Math.max(minHeight, resizeStartHeight + deltaY));
                }
                if (resizeCursor == Cursor.W_RESIZE || resizeCursor == Cursor.NW_RESIZE || resizeCursor == Cursor.SW_RESIZE) {
                    double newWidth = Math.max(minWidth, resizeStartWidth - deltaX);
                    double newX = resizeStartWindowX + (resizeStartWidth - newWidth);
                    stage.setWidth(newWidth);
                    stage.setX(newX);
                }
                if (resizeCursor == Cursor.N_RESIZE || resizeCursor == Cursor.NE_RESIZE || resizeCursor == Cursor.NW_RESIZE) {
                    double newHeight = Math.max(minHeight, resizeStartHeight - deltaY);
                    double newY = resizeStartWindowY + (resizeStartHeight - newHeight);
                    stage.setHeight(newHeight);
                    stage.setY(newY);
                }
            });

            scene.setOnMouseReleased(event -> resizing = false);
        }

        private VBox createBottomPanel() {
            VBox footerTitleBox = new VBox(6, footerSongLabel);
            footerTitleBox.setAlignment(Pos.CENTER_LEFT);
            footerTitleBox.setPadding(new Insets(8, 0, 0, 0));
            HBox trackCard = new HBox(12, footerArtLabel, footerTitleBox);
            trackCard.setAlignment(Pos.CENTER_LEFT);
            trackCard.setMinWidth(220);
            trackCard.setPrefWidth(240);

            HBox leftControls = new HBox(14, shuffleButton, previousButton);
            leftControls.setAlignment(Pos.CENTER_RIGHT);
            leftControls.setMinWidth(126);
            leftControls.setPrefWidth(126);
            leftControls.setMaxWidth(126);

            HBox rightControls = new HBox(14, nextButton, loopButton, repeatButton);
            rightControls.setAlignment(Pos.CENTER_LEFT);
            rightControls.setMinWidth(126);
            rightControls.setPrefWidth(126);
            rightControls.setMaxWidth(126);

            HBox topControls = new HBox(14, leftControls, playButton, rightControls);
            topControls.setAlignment(Pos.CENTER);

            HBox progressRow = new HBox(12, elapsedTimeLabel, createProgressBar(), totalTimeLabel);
            progressRow.setAlignment(Pos.CENTER);

            VBox centerTransport = new VBox(10, topControls, progressRow);
            centerTransport.setAlignment(Pos.CENTER);
            centerTransport.setMinWidth(360);
            centerTransport.setPrefWidth(500);
            HBox.setHgrow(centerTransport, Priority.ALWAYS);

            HBox volumeRow = new HBox(10, muteButton, createVolumeBar(), volumeValueLabel);
            volumeRow.setAlignment(Pos.CENTER_RIGHT);
            volumeRow.setMinWidth(220);
            volumeRow.setPrefWidth(240);

            StackPane centerWrap = new StackPane(centerTransport);
            centerWrap.setPadding(new Insets(0, 18, 0, 18));

            BorderPane footerShell = new BorderPane();
            footerShell.setLeft(trackCard);
            footerShell.setCenter(centerWrap);
            footerShell.setRight(volumeRow);
            BorderPane.setAlignment(trackCard, Pos.CENTER_LEFT);
            BorderPane.setAlignment(centerWrap, Pos.CENTER);
            BorderPane.setAlignment(volumeRow, Pos.CENTER_RIGHT);
            BorderPane.setMargin(trackCard, new Insets(0, 26, 0, 0));
            BorderPane.setMargin(volumeRow, new Insets(0, 0, 0, 26));

            VBox panel = new VBox(footerShell);
            panel.setPadding(new Insets(16, 20, 18, 20));
            return panel;
        }

        private StackPane createProgressBar() {
            progressBarControl = createCustomBar(progressBarBase, progressBarFill, progressBarThumb, true);
            HBox.setHgrow(progressBarControl, Priority.ALWAYS);
            return progressBarControl;
        }

        private StackPane createVolumeBar() {
            volumeBarControl = createCustomBar(volumeBarBase, volumeBarFill, volumeBarThumb, false);
            volumeBarControl.setPrefWidth(150);
            volumeBarControl.setMaxWidth(160);
            return volumeBarControl;
        }

        private StackPane createCustomBar(Region base, Region fill, Region thumb, boolean progressBar) {
            base.setManaged(false);
            fill.setManaged(false);
            thumb.setManaged(false);
            base.setMouseTransparent(true);
            fill.setMouseTransparent(true);
            thumb.setMouseTransparent(true);

            progressHoverLabel.setManaged(false);
            progressHoverLabel.setMouseTransparent(false);
            progressHoverLabel.setPickOnBounds(true);
            progressHoverLabel.setOnMouseEntered(event -> progressHoverLabel.setVisible(false));
            progressHoverLabel.setOnMouseMoved(event -> event.consume());
            progressHoverLabel.setOnMousePressed(event -> event.consume());
            progressHoverLabel.setOnMouseDragged(event -> event.consume());
            progressHoverLabel.setOnMouseReleased(event -> event.consume());

            StackPane shell = progressBar
                    ? new StackPane(base, fill, thumb, progressHoverLabel)
                    : new StackPane(base, fill, thumb);
            shell.setAlignment(Pos.CENTER_LEFT);
            shell.setPickOnBounds(true);
            shell.setMinHeight(progressBar ? 14 : 18);
            shell.setPrefHeight(progressBar ? 14 : 18);
            shell.setMaxHeight(progressBar ? 14 : 18);
            shell.setOnMousePressed(event -> {
                double fraction = clampFraction(event.getX(), shell.getWidth());
                if (progressBar) {
                    beginSeek(fraction);
                } else {
                    setVolumeFraction(fraction);
                }
            });
            shell.setOnMouseDragged(event -> {
                double fraction = clampFraction(event.getX(), shell.getWidth());
                if (progressBar) {
                    previewSeekFraction(fraction);
                } else {
                    setVolumeFraction(fraction);
                }
            });
            shell.setOnMouseReleased(event -> {
                if (progressBar) {
                    double fraction = clampFraction(event.getX(), shell.getWidth());
                    finishSeek(fraction);
                }
            });
            shell.setOnMouseMoved(event -> {
                if (progressBar) {
                    updateProgressHover(shell, event.getX());
                    updateThumbHover(shell, thumb, event.getX());
                } else {
                    updateThumbHover(shell, thumb, event.getX(), volumeSlider.getValue(), volumeSlider.getMax());
                }
            });
            shell.setOnMouseEntered(event -> {
                if (progressBar) {
                    animateNodeScale(thumb, 1.12);
                } else {
                    animateNodeScale(thumb, 1.12);
                }
            });
            shell.setOnMouseExited(event -> {
                if (progressBar) {
                    progressHoverLabel.setVisible(false);
                }
                animateNodeScale(thumb, 1.0);
            });
            shell.widthProperty().addListener((obs, oldValue, newValue) -> updateCustomBars());
            shell.heightProperty().addListener((obs, oldValue, newValue) -> updateCustomBars());
            return shell;
        }

        private VBox createPlaylistPanel() {
            HBox toolsRow = new HBox(12, searchLabel, searchField, addSongButton, addFolderButton, removeSongButton, clearPlaylistButton, undoButton);
            toolsRow.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(searchField, Priority.ALWAYS);
            searchField.setPromptText("Search tracks or folders...");

            VBox libraryPanel = new VBox(18, sectionLabel, toolsRow, playlistView);
            VBox.setVgrow(playlistView, Priority.ALWAYS);
            libraryPanel.setPadding(new Insets(22, 22, 18, 22));

            ScrollPane settingsPanel = createSettingsPanel();

            Tab libraryTab = new Tab("Library", libraryPanel);
            libraryTab.setClosable(false);
            Tab settingsTab = new Tab("Settings", settingsPanel);
            settingsTab.setClosable(false);

            tabPane = new TabPane(libraryTab, settingsTab);
            tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

            VBox wrapper = new VBox(tabPane);
            VBox.setVgrow(tabPane, Priority.ALWAYS);
            wrapper.setPadding(new Insets(18, 18, 14, 18));
            return wrapper;
        }

        private ScrollPane createSettingsPanel() {
            Label settingsTitle = new Label("Tune The Loadout");
            settingsTitle.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: 800;");

            Label settingsSubtitle = new Label("Push the interface harder with brighter color, bigger hero space, and snappier presentation.");
            settingsSubtitle.setStyle("-fx-text-fill: #a8b9ca; -fx-font-size: 13px;");

            appearanceCard = new VBox(
                    16,
                    createCardTitle("Visual Style"),
                    createColorSection(),
                    createPreviewCard(),
                    createSettingRow(radiusLabel, cornerRadiusSlider),
                    createSettingRow(heroWidthLabel, heroWidthSlider)
            );
            appearanceCard.setPadding(new Insets(22));
            appearanceCard.setStyle("-fx-background-color: linear-gradient(to bottom right, #182231, #0f151d); -fx-background-radius: 24; -fx-border-color: rgba(74,154,255,0.38); -fx-border-radius: 24;");

            Label animationLabel = new Label("SHOW MOTION");
            animationLabel.setStyle("-fx-text-fill: #c1c8cf; -fx-font-size: 12px; -fx-font-weight: 700;");

            Label hintLabel = new Label("Preferences stay local, so every machine can keep its own vibe.");
            hintLabel.setStyle("-fx-text-fill: #91a2b5; -fx-font-size: 12px;");

            behaviorCard = new VBox(
                    16,
                    createCardTitle("Flow"),
                    createSettingRow(animationLabel, animationsCheckBox),
                    hintLabel
            );
            behaviorCard.setPadding(new Insets(22));
            behaviorCard.setStyle("-fx-background-color: linear-gradient(to bottom right, #1d1a23, #131118); -fx-background-radius: 24; -fx-border-color: rgba(255,196,67,0.28); -fx-border-radius: 24;");

            VBox settingsContent = new VBox(18, settingsTitle, settingsSubtitle, appearanceCard, behaviorCard);
            settingsContent.setPadding(new Insets(24));
            settingsContent.setStyle("-fx-background-color: transparent;");

            ScrollPane scrollPane = new ScrollPane(settingsContent);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(false);
            scrollPane.setPannable(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
            return scrollPane;
        }

        private VBox createPreviewCard() {
            previewTitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: 800;");
            previewBodyLabel.setWrapText(true);
            previewBodyLabel.setStyle("-fx-text-fill: #b8c8d8; -fx-font-size: 12px;");
            previewChipLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 800;");

            VBox previewBox = new VBox(10, colorPreview, previewTitleLabel, previewBodyLabel, previewChipLabel);
            previewBox.setPadding(new Insets(18));
            return previewBox;
        }

        private VBox createColorSection() {
            colorHelpLabel.setWrapText(true);
            colorHelpLabel.setStyle("-fx-text-fill: #a3b8cc; -fx-font-size: 12px;");
            colorSwatch.setMinHeight(58);
            colorSwatch.setPrefHeight(58);
            colorPalette.setPrefWrapLength(300);

            VBox box = new VBox(12, accentLabel, colorPalette, colorPicker, colorSwatch, colorHelpLabel);
            box.setPadding(new Insets(4, 0, 2, 0));
            return box;
        }

        private void populateColorPalette() {
            colorPalette.getChildren().clear();
            String[] presets = {
                    "#2d7cff", "#34d8ff", "#ffd54a", "#ff8c42", "#ff5d73", "#78f06d",
                    "#20c997", "#00bcd4", "#4f7cff", "#9b6bff", "#ffffff", "#111827"
            };

            for (String preset : presets) {
                Button swatchButton = new Button();
                swatchButton.setMinSize(32, 32);
                swatchButton.setPrefSize(32, 32);
                swatchButton.setMaxSize(32, 32);
                swatchButton.setStyle(
                        "-fx-background-color: " + preset + ";" +
                        "-fx-background-radius: 999;" +
                        "-fx-border-color: rgba(255,255,255,0.14);" +
                        "-fx-border-radius: 999;" +
                        "-fx-padding: 0;" +
                        "-fx-cursor: hand;"
                );
                swatchButton.setOnAction(event -> applyAccentColor(Color.web(preset)));
                Tooltip.install(swatchButton, new Tooltip(preset));
                colorPalette.getChildren().add(swatchButton);
            }
        }

        private void applyAccentColor(Color color) {
            accentColor = color;
            colorPicker.setValue(color);
            preferences.put(PREF_ACCENT, toHex(accentColor));
            applyTheme();
        }

        private HBox createSettingRow(Label label, Node control) {
            label.setMinWidth(110);
            HBox row = new HBox(18, label, control);
            row.setAlignment(Pos.CENTER_LEFT);
            if (control instanceof Slider slider) {
                HBox.setHgrow(slider, Priority.ALWAYS);
            }
            return row;
        }

        private Label createCardTitle(String text) {
            Label title = new Label(text);
            title.setStyle("-fx-text-fill: white; -fx-font-size: 15px; -fx-font-weight: 800;");
            return title;
        }

        private Label createPlaceholder() {
            Label placeholder = new Label("No tracks in the locker yet.\nUse Add Song or Add Folder to load your local files.");
            placeholder.setAlignment(Pos.CENTER);
            placeholder.setStyle(
                    "-fx-text-fill: #98abc0;" +
                    "-fx-font-size: 15px;" +
                    "-fx-line-spacing: 5px;"
            );
            return placeholder;
        }

        private void styleLabels() {
            String accent = toHex(accentColor);
            appTitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 38px; -fx-font-weight: 900;");
            statusLabel.setStyle("-fx-text-fill: #b7c7d8; -fx-font-size: 14px; -fx-font-weight: 600;");
            nowPlayingCaption.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 11px; -fx-font-weight: 800;");
            nowPlayingLabel.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: 800;");
            songCountLabel.setStyle("-fx-text-fill: #9bb0c7; -fx-font-size: 13px; -fx-font-weight: 700;");
            sectionLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: 900;");
            searchLabel.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 11px; -fx-font-weight: 800;");
            accentLabel.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 11px; -fx-font-weight: 800;");
            radiusLabel.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 11px; -fx-font-weight: 800;");
            heroWidthLabel.setStyle("-fx-text-fill: " + accent + "; -fx-font-size: 11px; -fx-font-weight: 800;");
            colorPreview.setStyle("-fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: 800; -fx-padding: 12 16 12 16;");
            footerSongLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: 900;");
            windowIconGlyph.setFill(Color.web(accent));
            windowIconLabel.setStyle("-fx-padding: 0 2 0 0;");
            windowTitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: 800;");
        }

        private void stylePlaylist() {
            playlistView.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #131a23, #0e141b);" +
                    "-fx-control-inner-background: #111821;" +
                    "-fx-background-insets: 0;" +
                    "-fx-background-radius: 24;" +
                    "-fx-border-color: " + withOpacity(accentColor, 0.20) + ";" +
                    "-fx-border-radius: 24;" +
                    "-fx-padding: 12;"
            );
        }

        private void styleBaseButton(Button button, boolean accent) {
            String background = accent ? "#1ed760" : "#232932";
            String textColor = accent ? "#08110b" : "white";

            button.setStyle(
                    "-fx-background-color: " + background + ";" +
                    "-fx-text-fill: " + textColor + ";" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: 700;" +
                    "-fx-background-radius: 0;" +
                    "-fx-padding: 12 22 12 22;" +
                    "-fx-cursor: hand;"
            );
        }

        private void updateLoopButtonStyle() {
            String accentDark = darken(accentColor, 0.55);
            if (loopEnabled) {
                styleModeButton(loopButton, accentDark);
            } else {
                styleModeButton(loopButton, "#232932");
            }
        }

        private void updateSongCountLabel() {
            int count = playlist.size();
            songCountLabel.setText(count + (count == 1 ? " track loaded" : " tracks loaded"));
            clearPlaylistButton.setDisable(count == 0);
        }

        private void installTooltips() {
            Tooltip.install(shuffleButton, new Tooltip("Shuffle playlist"));
            Tooltip.install(previousButton, new Tooltip("Previous song"));
            Tooltip.install(playButton, new Tooltip("Start or stop playback"));
            Tooltip.install(nextButton, new Tooltip("Next song"));
            Tooltip.install(loopButton, new Tooltip("Repeat current song"));
            Tooltip.install(repeatButton, new Tooltip("Repeat whole playlist"));
            Tooltip.install(muteButton, new Tooltip("Mute volume"));
            Tooltip.install(addSongButton, new Tooltip("Add audio files"));
            Tooltip.install(addFolderButton, new Tooltip("Add all songs from a folder"));
            Tooltip.install(removeSongButton, new Tooltip("Remove selected song"));
            Tooltip.install(clearPlaylistButton, new Tooltip("Clear the whole playlist"));
            Tooltip.install(undoButton, new Tooltip("Undo last remove or clear"));
            Tooltip.install(minimizeWindowButton, new Tooltip("Minimize window"));
            Tooltip.install(maximizeWindowButton, new Tooltip("Maximize or restore window"));
            Tooltip.install(closeWindowButton, new Tooltip("Close window"));
            installButtonHoverScale(shuffleButton);
            installButtonHoverScale(previousButton);
            installButtonHoverScale(playButton);
            installButtonHoverScale(nextButton);
            installButtonHoverScale(loopButton);
            installButtonHoverScale(repeatButton);
            installButtonHoverScale(muteButton);
            installButtonHoverScale(addSongButton);
            installButtonHoverScale(addFolderButton);
            installButtonHoverScale(removeSongButton);
            installButtonHoverScale(clearPlaylistButton);
            installButtonHoverScale(undoButton);
            installWindowButtonHover(minimizeWindowButton, "#1a2027");
            installWindowButtonHover(maximizeWindowButton, "#1a2027");
            installWindowButtonHover(closeWindowButton, "#5c2a2a");
        }

        private void installButtonHoverScale(Button button) {
            button.setOnMouseEntered(event -> animateNodeScale(button, button == playButton ? 1.08 : 1.06));
            button.setOnMouseExited(event -> animateNodeScale(button, 1.0));
        }

        private void installWindowButtonHover(Button button, String hoverColor) {
            button.setOnMouseEntered(event -> {
                button.setStyle(button.getStyle() + "-fx-background-color: " + hoverColor + ";");
                animateNodeScale(button, 1.04);
            });
            button.setOnMouseExited(event -> {
                applyTheme();
                animateNodeScale(button, 1.0);
            });
        }

        private void setUndoAction(UndoAction action) {
            lastUndoAction = action;
            undoButton.setDisable(action.kind() == UndoKind.NONE);
            if (root != null) {
                applyTheme();
            }
        }

        private void undoLastAction() {
            if (lastUndoAction.kind() == UndoKind.NONE) {
                return;
            }

            if (lastUndoAction.kind() == UndoKind.REMOVE) {
                int insertAt = Math.max(0, Math.min(lastUndoAction.insertIndex(), playlist.size()));
                playlist.addAll(insertAt, lastUndoAction.files());
                currentIndex = currentIndex >= insertAt && currentIndex >= 0
                        ? currentIndex + lastUndoAction.files().size()
                        : currentIndex;
                statusLabel.setText("Restored removed song");
            } else if (lastUndoAction.kind() == UndoKind.CLEAR) {
                playlist.setAll(lastUndoAction.files());
                currentIndex = lastUndoAction.previousCurrentIndex() >= 0 && lastUndoAction.previousCurrentIndex() < playlist.size()
                        ? lastUndoAction.previousCurrentIndex()
                        : (playlist.isEmpty() ? -1 : 0);
                statusLabel.setText("Restored cleared playlist");
            }

            savePlaylist();
            updateSongCountLabel();
            playlistView.refresh();
            updateFooterTrackInfo();
            updateBarProgress();
            if (currentIndex >= 0 && currentIndex < playlist.size()) {
                File selectedFile = playlist.get(currentIndex);
                if (filteredPlaylist.contains(selectedFile)) {
                    playlistView.getSelectionModel().select(selectedFile);
                } else if (!filteredPlaylist.isEmpty()) {
                    playlistView.getSelectionModel().selectFirst();
                }
            }
            setUndoAction(UndoAction.none());
        }

        private void configureSliders() {
            progressSlider.setDisable(true);
            volumeSlider.setValue(preferences.getDouble(PREF_VOLUME, 65));
            updateVolumeValueLabel();
            volumeSlider.valueProperty().addListener((obs, oldValue, newValue) -> {
                updateVolumeValueLabel();
                applyVolume();
                updateCustomBars();
            });
        }

        private void styleSliders() {
            elapsedTimeLabel.setStyle("-fx-text-fill: #9aa3ad; -fx-font-size: 12px;");
            totalTimeLabel.setStyle("-fx-text-fill: #9aa3ad; -fx-font-size: 12px;");
            volumeValueLabel.setStyle("-fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: 700;");
            volumeValueLabel.setMinWidth(40);
            volumeValueLabel.setPrefWidth(40);
            volumeValueLabel.setAlignment(Pos.CENTER_RIGHT);

            String sliderStyle =
                    "-fx-control-inner-background: transparent;" +
                    "-fx-background-color: transparent;";
            progressSlider.setStyle(sliderStyle);
            volumeSlider.setStyle(sliderStyle);
            cornerRadiusSlider.setStyle(sliderStyle);
            heroWidthSlider.setStyle(sliderStyle);
        }

        private void applyVolume() {
            if (mediaPlayer != null) {
                double normalized = volumeSlider.getValue() / 100.0;
                double curved = normalized <= 0.0
                        ? 0.0
                        : Math.pow(normalized, 2.55) * 0.72;
                mediaPlayer.setVolume(Math.min(1.0, curved));
            }
            muted = volumeSlider.getValue() <= 0.01;
            if (!muted && volumeSlider.getValue() > 0) {
                lastVolumeBeforeMute = volumeSlider.getValue();
            }
            updateMuteButtonLabel();
            updateCustomBars();
        }

        private double clampFraction(double x, double width) {
            if (width <= 0) {
                return 0;
            }
            return Math.max(0, Math.min(1, x / width));
        }

        private void updateProgressHover(StackPane shell, double x) {
            if (mediaPlayer == null) {
                progressHoverLabel.setVisible(false);
                return;
            }

            Duration total = mediaPlayer.getTotalDuration();
            if (total == null || total.isUnknown() || total.lessThanOrEqualTo(Duration.ZERO)) {
                progressHoverLabel.setVisible(false);
                return;
            }

            double fraction = clampFraction(x, shell.getWidth());
            Duration previewTime = Duration.seconds(total.toSeconds() * fraction);
            progressHoverLabel.setText(formatDuration(previewTime));
            progressHoverLabel.applyCss();
            progressHoverLabel.autosize();

            double labelWidth = progressHoverLabel.getWidth();
            double labelHeight = progressHoverLabel.getHeight();
            double labelX = Math.max(0, Math.min(shell.getWidth() - labelWidth, x - (labelWidth / 2.0)));
            double labelY = -labelHeight - 6;

            progressHoverLabel.resizeRelocate(labelX, labelY, labelWidth, labelHeight);
            progressHoverLabel.setVisible(true);
        }

        private void updateThumbHover(StackPane shell, Region thumb, double x) {
            updateThumbHover(shell, thumb, x, progressSlider.getValue(), progressSlider.getMax());
        }

        private void updateThumbHover(StackPane shell, Region thumb, double x, double value, double max) {
            double width = shell.getWidth();
            if (width <= 0) {
                animateNodeScale(thumb, 1.0);
                return;
            }

            double ratio = max <= 0 ? 0 : Math.max(0, Math.min(1, value / max));
            double thumbCenter = width * ratio;
            double distance = Math.abs(x - thumbCenter);
            animateNodeScale(thumb, distance <= 14 ? 1.18 : 1.0);
        }

        private void animateNodeScale(Node node, double targetScale) {
            ScaleTransition transition = new ScaleTransition(Duration.millis(120), node);
            transition.setToX(targetScale);
            transition.setToY(targetScale);
            transition.play();
        }

        private void beginSeek(double fraction) {
            if (mediaPlayer == null) {
                return;
            }

            isSeeking = true;
            resumeAfterSeek = mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
            if (resumeAfterSeek) {
                mediaPlayer.pause();
            }
            previewSeekFraction(fraction);
        }

        private void previewSeekFraction(double fraction) {
            if (mediaPlayer == null) {
                return;
            }

            Duration total = mediaPlayer.getTotalDuration();
            if (total == null || total.isUnknown() || total.lessThanOrEqualTo(Duration.ZERO)) {
                return;
            }

            double seconds = total.toSeconds() * fraction;
            progressSlider.setMax(total.toSeconds());
            progressSlider.setValue(seconds);
            elapsedTimeLabel.setText(formatDuration(Duration.seconds(seconds)));
            totalTimeLabel.setText(formatDuration(total));
            updateCustomBars();
        }

        private void finishSeek(double fraction) {
            if (mediaPlayer == null) {
                isSeeking = false;
                resumeAfterSeek = false;
                return;
            }

            Duration total = mediaPlayer.getTotalDuration();
            if (total != null && !total.isUnknown() && total.greaterThan(Duration.ZERO)) {
                double seconds = total.toSeconds() * fraction;
                mediaPlayer.seek(Duration.seconds(seconds));
                progressSlider.setMax(total.toSeconds());
                progressSlider.setValue(seconds);
            }

            isSeeking = false;
            if (resumeAfterSeek) {
                mediaPlayer.play();
            }
            resumeAfterSeek = false;
            updateCustomBars();
        }

        private void setVolumeFraction(double fraction) {
            volumeSlider.setValue(fraction * volumeSlider.getMax());
            updateCustomBars();
        }

        private void updateProgress(Duration currentTime) {
            if (mediaPlayer == null) {
                return;
            }

            Duration total = mediaPlayer.getTotalDuration();
            if (total == null || total.isUnknown() || total.lessThanOrEqualTo(Duration.ZERO)) {
                return;
            }

            elapsedTimeLabel.setText(formatDuration(currentTime));
            totalTimeLabel.setText(formatDuration(total));

            if (!isSeeking && !progressSlider.isValueChanging()) {
                progressSlider.setMax(total.toSeconds());
                progressSlider.setValue(currentTime.toSeconds());
            }
            updateCustomBars();
        }

        private String formatDuration(Duration duration) {
            if (duration == null || duration.isUnknown() || duration.lessThan(Duration.ZERO)) {
                return "00:00";
            }

            int totalSeconds = (int) Math.floor(duration.toSeconds());
            int minutes = totalSeconds / 60;
            int seconds = totalSeconds % 60;
            return String.format(Locale.ROOT, "%02d:%02d", minutes, seconds);
        }

        private void updateVolumeValueLabel() {
            volumeValueLabel.setText((int) Math.round(volumeSlider.getValue()) + "%");
        }

        private void updateBarProgress() {
            // No-op after reverting custom accent bar rendering.
        }

        private void updateCustomBars() {
            updateCustomBar(progressBarControl, progressBarBase, progressBarFill, progressBarThumb, progressSlider.getValue(), progressSlider.getMax());
            updateCustomBar(volumeBarControl, volumeBarBase, volumeBarFill, volumeBarThumb, volumeSlider.getValue(), volumeSlider.getMax());
        }

        private void updateCustomBar(StackPane shell, Region base, Region fill, Region thumb, double value, double max) {
            if (shell == null) {
                return;
            }

            double width = shell.getWidth();
            if (width <= 0) {
                return;
            }

            double ratio = max <= 0 ? 0 : Math.max(0, Math.min(1, value / max));
            double barHeight = shell == progressBarControl ? 6 : 8;
            double thumbSize = shell == progressBarControl ? 12 : 14;
            double fillWidth = width * ratio;
            double thumbX = Math.max(0, Math.min(width - thumbSize, fillWidth - (thumbSize / 2.0)));
            double barY = (shell.getHeight() - barHeight) / 2.0;
            double thumbY = (shell.getHeight() - thumbSize) / 2.0;

            base.resizeRelocate(0, barY, width, barHeight);
            fill.resizeRelocate(0, barY, fillWidth, barHeight);
            thumb.resizeRelocate(thumbX, thumbY, thumbSize, thumbSize);
        }


        private void loadSavedPlaylist() {
            String saved = preferences.get(PREF_PLAYLIST, "");
            if (saved.isBlank()) {
                updateSongCountLabel();
                updateBarProgress();
                return;
            }

            String[] entries = saved.split("\\R");
            List<File> restored = new ArrayList<>();
            for (String entry : entries) {
                if (entry.isBlank()) {
                    continue;
                }
                File file = new File(entry);
                if (file.exists() && file.isFile()) {
                    restored.add(file);
                }
            }

            playlist.setAll(restored);
            updateSongCountLabel();
            if (!playlist.isEmpty()) {
                currentIndex = 0;
                if (!filteredPlaylist.isEmpty()) {
                    playlistView.getSelectionModel().selectFirst();
                }
            }
            updateFooterTrackInfo();
            updateBarProgress();
        }

        private void savePlaylist() {
            StringBuilder builder = new StringBuilder();
            for (File file : playlist) {
                if (builder.length() > 0) {
                    builder.append(System.lineSeparator());
                }
                builder.append(file.getAbsolutePath());
            }
            preferences.put(PREF_PLAYLIST, builder.toString());
        }

        private void configureSearch() {
            searchField.setPromptText("Type to filter songs...");
            searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                String term = newValue == null ? "" : newValue.trim().toLowerCase(Locale.ROOT);
                filteredPlaylist.setPredicate(file -> term.isEmpty()
                        || file.getName().toLowerCase(Locale.ROOT).contains(term)
                        || file.getParent().toLowerCase(Locale.ROOT).contains(term));
                playlistView.refresh();
            });
            searchField.setOnAction(event -> playSelectedOrCurrentSong());
        }

        private void addFolder(Stage stage) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose Music Folder");
            File selectedDirectory = directoryChooser.showDialog(stage);
            if (selectedDirectory == null || !selectedDirectory.isDirectory()) {
                return;
            }

            File[] audioFiles = selectedDirectory.listFiles(file -> file.isFile() && isSupportedAudioFile(file));
            if (audioFiles == null || audioFiles.length == 0) {
                showError("No audio files found", "The selected folder does not contain .mp3 or .wav files.");
                return;
            }

            addFilesToPlaylist(List.of(audioFiles));
        }

        private void addFilesToPlaylist(List<File> files) {
            for (File file : files) {
                if (isSupportedAudioFile(file) && !playlist.contains(file)) {
                    playlist.add(file);
                }
            }

            updateSongCountLabel();
            savePlaylist();
            playlistView.refresh();

            if (currentIndex < 0 && !playlist.isEmpty()) {
                currentIndex = 0;
            }
            if (!filteredPlaylist.isEmpty()) {
                playlistView.getSelectionModel().selectFirst();
            }
            updateFooterTrackInfo();
            updateBarProgress();
        }

        private void installDragAndDrop(Node target) {
            target.setOnDragOver(event -> {
                if (event.getGestureSource() != target && event.getDragboard().hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
                event.consume();
            });

            target.setOnDragDropped(event -> {
                boolean success = false;
                if (event.getDragboard().hasFiles()) {
                    addFilesToPlaylist(event.getDragboard().getFiles());
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }

        private void removeSelectedSong() {
            File selectedFile = playlistView.getSelectionModel().getSelectedItem();
            if (selectedFile == null) {
                return;
            }

            int removedIndex = playlist.indexOf(selectedFile);
            boolean wasCurrent = currentIndex >= 0 && currentIndex < playlist.size() && playlist.get(currentIndex).equals(selectedFile);
            setUndoAction(new UndoAction(UndoKind.REMOVE, List.of(selectedFile), removedIndex, currentIndex));
            playlist.remove(selectedFile);
            savePlaylist();
            updateSongCountLabel();
            playlistView.refresh();

            if (playlist.isEmpty()) {
                currentIndex = -1;
                disposeCurrentPlayer();
                nowPlayingLabel.setText("Nothing selected");
                updateFooterTrackInfo();
                statusLabel.setText("Drop tracks. Hit play. Stay loud.");
                updatePlayButtonLabel();
                previousButton.setDisable(true);
                nextButton.setDisable(true);
                progressSlider.setDisable(true);
                progressSlider.setValue(0);
                elapsedTimeLabel.setText("00:00");
                totalTimeLabel.setText("00:00");
                updateBarProgress();
                previousButton.setDisable(true);
                nextButton.setDisable(true);
                return;
            }

            if (wasCurrent) {
                currentIndex = Math.min(removedIndex, playlist.size() - 1);
                playSongAt(currentIndex);
            } else if (removedIndex < currentIndex) {
                currentIndex--;
            }

            if (!filteredPlaylist.isEmpty()) {
                playlistView.getSelectionModel().selectFirst();
            }
            updateFooterTrackInfo();
            updateBarProgress();
        }

        private void clearPlaylist() {
            if (!playlist.isEmpty()) {
                setUndoAction(new UndoAction(UndoKind.CLEAR, new ArrayList<>(playlist), 0, currentIndex));
            }
            playlist.clear();
            savePlaylist();
            searchField.clear();
            currentIndex = -1;
            disposeCurrentPlayer();
            playlistView.getSelectionModel().clearSelection();
            updateSongCountLabel();
            removeSongButton.setDisable(true);
            updatePlayButtonLabel();
            previousButton.setDisable(true);
            nextButton.setDisable(true);
            progressSlider.setDisable(true);
            progressSlider.setValue(0);
            elapsedTimeLabel.setText("00:00");
            totalTimeLabel.setText("00:00");
            nowPlayingLabel.setText("Nothing selected");
            updateFooterTrackInfo();
            statusLabel.setText("Drop tracks. Hit play. Stay loud.");
            updateBarProgress();
        }

        private void restoreWindowBounds(Stage stage) {
            double width = Math.max(1080, preferences.getDouble(PREF_WINDOW_W, 980));
            double height = Math.max(560, preferences.getDouble(PREF_WINDOW_H, 620));
            double x = preferences.getDouble(PREF_WINDOW_X, Double.NaN);
            double y = preferences.getDouble(PREF_WINDOW_Y, Double.NaN);
            var bounds = Screen.getPrimary().getVisualBounds();

            double safeWidth = Math.min(width, bounds.getWidth());
            double safeHeight = Math.min(height, bounds.getHeight());
            stage.setWidth(safeWidth);
            stage.setHeight(safeHeight);

            double defaultX = bounds.getMinX() + Math.max(0, (bounds.getWidth() - safeWidth) / 2);
            double defaultY = bounds.getMinY() + Math.max(0, (bounds.getHeight() - safeHeight) / 2);

            double safeX = Double.isNaN(x) ? defaultX : x;
            double safeY = Double.isNaN(y) ? defaultY : y;

            if (safeX < bounds.getMinX() || safeX + safeWidth > bounds.getMaxX()) {
                safeX = defaultX;
            }
            if (safeY < bounds.getMinY() || safeY + safeHeight > bounds.getMaxY()) {
                safeY = defaultY;
            }

            stage.setX(safeX);
            stage.setY(safeY);

            restoredX = safeX;
            restoredY = safeY;
            restoredWidth = safeWidth;
            restoredHeight = safeHeight;
        }

        private void restoreMaximizedState(Stage stage) {
            if (!preferences.getBoolean(PREF_WINDOW_MAXIMIZED, false)) {
                return;
            }
            toggleWindowMaximize(stage);
        }

        private void saveWindowBounds(double x, double y, double width, double height) {
            preferences.putDouble(PREF_WINDOW_X, x);
            preferences.putDouble(PREF_WINDOW_Y, y);
            preferences.putDouble(PREF_WINDOW_W, width);
            preferences.putDouble(PREF_WINDOW_H, height);
        }

        private void applyTheme() {
            String accent = toHex(accentColor);
            String accentDark = darken(accentColor, 0.55);
            String accentGlow = withOpacity(accentColor, 0.18);
            String accentSoft = withOpacity(accentColor, 0.05);
            String accentDeep = withOpacity(accentColor, 0.08);

            root.setStyle("-fx-background-color: linear-gradient(to bottom right, #08111b, #101827 45%, #15111f 100%); -fx-background-insets: 0;");
            double radius = cornerRadiusSlider.getValue();
            titleBar.setStyle("-fx-background-color: linear-gradient(to right, rgba(7,14,24,0.98), rgba(14,21,34,0.96)); -fx-border-color: transparent transparent " + accentGlow + " transparent; -fx-border-width: 0 0 1 0;");
            heroPanel.setStyle("-fx-background-color: linear-gradient(to bottom, rgba(24,28,33,0.98), rgba(10,16,25,0.98)); -fx-border-color: transparent " + accentGlow + " transparent transparent; -fx-border-width: 0 1 0 0;");
            bottomPanel.setStyle("-fx-background-color: linear-gradient(to right, #121926, #0e1520); -fx-border-color: " + accentGlow + " transparent transparent transparent; -fx-border-width: 1 0 0 0;");
            if (tabPane != null) {
                tabPane.setStyle("-fx-background-color: transparent; -fx-tab-min-height: 40; -fx-accent: " + accent + ";");
            }
            if (appearanceCard != null) {
                appearanceCard.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(24,34,49,0.96), rgba(15,21,29,0.99)); -fx-background-radius: 24; -fx-border-color: " + withOpacity(accentColor, 0.24) + "; -fx-border-radius: 24;");
            }
            if (behaviorCard != null) {
                behaviorCard.setStyle("-fx-background-color: linear-gradient(to bottom right, rgba(29,26,35,0.96), rgba(19,17,24,0.99)); -fx-background-radius: 24; -fx-border-color: " + withOpacity(accentColor, 0.18) + "; -fx-border-radius: 24;");
            }

            styleLabels();
            stylePlaylist();
            styleSliders();
            searchField.setStyle("-fx-background-color: linear-gradient(to bottom, #132030, #101721); -fx-text-fill: white; -fx-prompt-text-fill: #7f9ab8; -fx-background-radius: " + (radius + 4) + "; -fx-border-color: " + withOpacity(accentColor, 0.24) + "; -fx-border-radius: " + (radius + 4) + "; -fx-padding: 0 14 0 14;");
            colorPicker.setStyle("-fx-background-color: linear-gradient(to bottom, " + accentDeep + ", rgba(21,27,38,0.98)); -fx-background-radius: " + radius + "; -fx-font-size: 14px;");
            colorPicker.setPrefWidth(320);
            colorPicker.setPrefHeight(50);
            animationsCheckBox.setStyle("-fx-text-fill: white; -fx-font-weight: 700;");
            progressHoverLabel.setStyle(
                    "-fx-background-color: rgba(9,17,27,0.95);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-padding: 6 8 6 8;" +
                    "-fx-border-color: " + withOpacity(accentColor, 0.45) + ";" +
                    "-fx-border-radius: 8;" +
                    "-fx-background-radius: 8;"
            );
            colorPreview.setStyle(
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 11px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-padding: 7 12 7 12;" +
                    "-fx-background-color: linear-gradient(to right, " + accent + ", " + accentDark + ");" +
                    "-fx-background-radius: " + Math.max(8, radius - 6) + ";"
            );
            previewTitleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: 800;");
            previewBodyLabel.setStyle("-fx-text-fill: #b8c8d8; -fx-font-size: 12px;");
            previewChipLabel.setStyle(
                    "-fx-background-color: linear-gradient(to right, " + accent + ", " + accentDark + ");" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-padding: 8 12 8 12;" +
                    "-fx-background-radius: " + Math.max(10, radius - 2) + ";"
            );
            if (colorPreview.getParent() instanceof Region previewCard) {
                previewCard.setStyle(
                        "-fx-background-color: linear-gradient(to bottom right, #182537, #121923 65%, #1b1623);" +
                        "-fx-border-color: " + withOpacity(accentColor, 0.28) + ";" +
                        "-fx-border-radius: " + (radius + 6) + ";" +
                        "-fx-background-radius: " + (radius + 6) + ";"
                );
            }
            colorSwatch.setStyle(
                    "-fx-background-color: linear-gradient(to right, " + darken(accentColor, 0.35) + ", " + accent + ", #ffffff);" +
                    "-fx-background-radius: " + (radius + 6) + ";" +
                    "-fx-border-color: " + withOpacity(accentColor, 0.60) + ";" +
                    "-fx-border-radius: " + (radius + 6) + ";"
            );
            colorPalette.setStyle("-fx-padding: 2 0 2 0;");

            playlistBadge.setStyle("-fx-background-color: linear-gradient(to right, " + accent + ", " + accentDark + "); -fx-text-fill: white; -fx-background-radius: " + Math.max(8, radius - 6) + "; -fx-padding: 7 12 7 12; -fx-font-size: 11px; -fx-font-weight: 900;");
            albumArt.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, " + accent + ", " + darken(accentColor, 0.35) + ");" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 88px;" +
                    "-fx-font-weight: 900;" +
                    "-fx-min-width: 210px;" +
                    "-fx-min-height: 210px;" +
                    "-fx-max-width: 210px;" +
                    "-fx-max-height: 210px;" +
                    "-fx-border-color: rgba(255,255,255,0.24);" +
                    "-fx-border-radius: " + (radius + 6) + ";" +
                    "-fx-background-radius: " + (radius + 6) + ";"
            );
            footerArtLabel.setStyle(
                    "-fx-background-color: linear-gradient(to bottom right, " + accent + ", " + darken(accentColor, 0.30) + ");" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 24px;" +
                    "-fx-font-weight: 900;" +
                    "-fx-alignment: center;" +
                    "-fx-min-width: 52px;" +
                    "-fx-min-height: 52px;" +
                    "-fx-max-width: 52px;" +
                    "-fx-max-height: 52px;" +
                    "-fx-background-radius: " + Math.max(10, radius - 2) + ";"
            );

            styleButton(addSongButton, accent, true);
            styleButton(addFolderButton, accentDark, false);
            styleButton(removeSongButton, "#1c2430", false);
            styleButton(clearPlaylistButton, "#1c2430", false);
            styleButton(undoButton, lastUndoAction.kind() == UndoKind.NONE ? "#1c2430" : accentDark, false);
            styleWindowButton(minimizeWindowButton, "#121a25");
            styleWindowButton(maximizeWindowButton, "#121a25");
            styleWindowButton(closeWindowButton, "#121a25");
            removeSongButton.setStyle(removeSongButton.getStyle() + "-fx-font-size: 16px; -fx-font-weight: 800;");
            clearPlaylistButton.setStyle(clearPlaylistButton.getStyle() + "-fx-font-size: 16px; -fx-font-weight: 800;");
            styleTransportButton(playButton, accent, true, true);
            styleControlButton(previousButton, "#20262e", 46, 13, 16, 9, 12);
            styleControlButton(nextButton, "#20262e", 46, 13, 16, 9, 12);
            updateRepeatButtonStyle();
            updateShuffleButtonStyle();
            styleControlButton(muteButton, muted ? accentDark : "#232932", 46, 15, 16, 9, 12);
            updateLoopButtonStyle();
            if (tabPane != null) {
                tabPane.setTabMinWidth(110);
                tabPane.setTabMaxWidth(110);
                tabPane.setTabMinHeight(40);
            }

            styleCustomBars(radius, accent);
            updateCustomBars();
            playlistView.refresh();
        }

        private void styleCustomBars(double radius, String accent) {
            String baseStyle =
                    "-fx-background-color: rgba(255,255,255,0.18);" +
                    "-fx-background-radius: " + radius + ";";
            String fillStyle =
                    "-fx-background-color: linear-gradient(to right, " + accent + ", #ffffff);" +
                    "-fx-background-radius: " + radius + ";";
            String thumbStyle =
                    "-fx-background-color: #ffffff;" +
                    "-fx-background-radius: 999;";

            progressBarBase.setStyle(baseStyle);
            volumeBarBase.setStyle(baseStyle);
            progressBarFill.setStyle(fillStyle);
            volumeBarFill.setStyle(fillStyle);
            progressBarThumb.setStyle(thumbStyle);
            volumeBarThumb.setStyle(thumbStyle);
        }

        private void styleButton(Button button, String background, boolean darkText) {
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " + background + ", " + darken(Color.web(background), 0.25) + ");" +
                    "-fx-text-fill: " + (darkText ? "#08110b" : "white") + ";" +
                    "-fx-font-size: 13px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-color: rgba(255,255,255,0.12);" +
                    "-fx-border-radius: 12;" +
                    "-fx-padding: 12 22 12 22;" +
                    "-fx-cursor: hand;"
            );
        }

        private void styleIconButton(Button button, String background) {
            styleControlButton(button, background, 46, 15, 16, 9, 12);
        }

        private void styleControlButton(Button button, String background, int width, int fontSize, int radius, int paddingY, int paddingX) {
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " + background + ", " + darken(Color.web(background), 0.22) + ");" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: " + fontSize + "px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-background-radius: " + Math.max(10, radius - 4) + ";" +
                    "-fx-border-color: rgba(255,255,255,0.12);" +
                    "-fx-border-radius: " + Math.max(10, radius - 4) + ";" +
                    "-fx-padding: " + paddingY + " " + paddingX + " " + paddingY + " " + paddingX + ";" +
                    "-fx-min-width: " + width + ";" +
                    "-fx-pref-width: " + width + ";" +
                    "-fx-max-width: " + width + ";" +
                    "-fx-cursor: hand;"
            );
        }

        private void styleWindowButton(Button button, String background) {
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " + background + ", #0d1118);" +
                    "-fx-text-fill: white;" +
                    "-fx-font-size: 12px;" +
                    "-fx-font-weight: 800;" +
                    "-fx-background-radius: 10;" +
                    "-fx-border-color: rgba(255,255,255,0.10);" +
                    "-fx-border-radius: 10;" +
                    "-fx-padding: 6 10 6 10;" +
                    "-fx-min-width: 34;" +
                    "-fx-pref-width: 34;" +
                    "-fx-max-width: 34;" +
                    "-fx-cursor: hand;"
            );
        }

        private void styleTransportButton(Button button, String background, boolean darkText, boolean primary) {
            button.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, " + background + ", " + darken(Color.web(background), 0.20) + ");" +
                    "-fx-text-fill: " + (darkText ? "#08110b" : "white") + ";" +
                    "-fx-font-size: " + (primary ? "17px" : "13px") + ";" +
                    "-fx-font-weight: 800;" +
                    "-fx-background-radius: " + (primary ? "16" : "12") + ";" +
                    "-fx-border-color: rgba(255,255,255,0.14);" +
                    "-fx-border-radius: " + (primary ? "16" : "12") + ";" +
                    "-fx-padding: " + (primary ? "12 14 12 14" : "9 10 9 10") + ";" +
                    "-fx-min-width: " + (primary ? "56" : "38") + ";" +
                    "-fx-pref-width: " + (primary ? "56" : "38") + ";" +
                    "-fx-max-width: " + (primary ? "56" : "38") + ";" +
                    "-fx-cursor: hand;"
            );
        }

        private void configurePlayButtonGraphic() {
            playButton.setText("");
            playButtonGlyph.setStroke(null);
            playButtonGlyph.setFill(Color.web("#08110b"));
            playButton.setGraphic(playButtonGlyph);
            updatePlayButtonLabel();
        }

        private void configureWindowIconGraphic() {
            windowIconGlyph.setContent("M 2 1 L 5 1 L 5 10 L 11 10 L 11 13 L 2 13 Z");
            windowIconGlyph.setStroke(null);
            windowIconLabel.setText("");
            windowIconLabel.setGraphic(windowIconGlyph);
        }

        private String toHex(Color color) {
            int red = (int) Math.round(color.getRed() * 255);
            int green = (int) Math.round(color.getGreen() * 255);
            int blue = (int) Math.round(color.getBlue() * 255);
            return String.format(Locale.ROOT, "#%02x%02x%02x", red, green, blue);
        }

        private String darken(Color color, double factor) {
            Color darker = color.interpolate(Color.BLACK, factor);
            return toHex(darker);
        }

        private String withOpacity(Color color, double opacity) {
            int red = (int) Math.round(color.getRed() * 255);
            int green = (int) Math.round(color.getGreen() * 255);
            int blue = (int) Math.round(color.getBlue() * 255);
            return String.format(Locale.ROOT, "rgba(%d,%d,%d,%.3f)", red, green, blue, opacity);
        }

        private void animateTrackChange() {
            if (nowPlayingLabel.getScene() == null) {
                return;
            }

            FadeTransition textFade = new FadeTransition(javafx.util.Duration.millis(220), nowPlayingLabel);
            textFade.setFromValue(0.45);
            textFade.setToValue(1.0);

            ScaleTransition textScale = new ScaleTransition(javafx.util.Duration.millis(220), nowPlayingLabel);
            textScale.setFromX(0.98);
            textScale.setFromY(0.98);
            textScale.setToX(1.0);
            textScale.setToY(1.0);

            FadeTransition artFade = new FadeTransition(javafx.util.Duration.millis(240), albumArt);
            artFade.setFromValue(0.55);
            artFade.setToValue(1.0);

            ScaleTransition artScale = new ScaleTransition(javafx.util.Duration.millis(240), albumArt);
            artScale.setFromX(0.94);
            artScale.setFromY(0.94);
            artScale.setToX(1.0);
            artScale.setToY(1.0);

            new ParallelTransition(textFade, textScale, artFade, artScale).play();
        }

        private boolean isSupportedAudioFile(File file) {
            String name = file.getName().toLowerCase(Locale.ROOT);
            return name.endsWith(".mp3") || name.endsWith(".wav");
        }

        private WritableImage createAppIcon(double size) {
            Canvas canvas = new Canvas(size, size);
            GraphicsContext gc = canvas.getGraphicsContext2D();

            gc.setFill(Color.TRANSPARENT);
            gc.fillRect(0, 0, size, size);

            gc.setFill(Color.web("#2b83bd"));
            double inset = size * 0.094;
            double circleSize = size - (inset * 2);
            gc.fillOval(inset, inset, circleSize, circleSize);

            gc.setFill(Color.BLACK);
            gc.fillRoundRect(size * 0.35, size * 0.28, size * 0.10, size * 0.38, size * 0.04, size * 0.04);
            gc.fillRoundRect(size * 0.35, size * 0.56, size * 0.28, size * 0.10, size * 0.04, size * 0.04);

            SnapshotParameters parameters = new SnapshotParameters();
            parameters.setFill(Color.TRANSPARENT);
            return canvas.snapshot(parameters, null);
        }

        private void updatePlayButtonLabel() {
            if (mediaPlayer == null) {
                setPlayButtonGlyph(false);
                return;
            }

            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PLAYING) {
                setPlayButtonGlyph(true);
            } else {
                setPlayButtonGlyph(false);
            }
        }

        private void setPlayButtonGlyph(boolean playing) {
            if (playing) {
                playButtonGlyph.setContent("M 3 3 L 13 3 L 13 13 L 3 13 Z");
                playButtonGlyph.setTranslateX(0);
                playButtonGlyph.setRotate(0);
            } else {
                playButtonGlyph.setContent("M 2 2 L 2 14 L 14 8 Z");
                playButtonGlyph.setTranslateX(1);
                playButtonGlyph.setRotate(0);
            }
        }

        private void updateLoopButtonLabel() {
            loopButton.setText("1X");
        }

        private void updateRepeatButtonLabel() {
            repeatButton.setText("ALL");
        }

        private void updateShuffleButtonLabel() {
            shuffleButton.setText("\u21c4");
        }

        private void updateMuteButtonLabel() {
            muteButton.setText(muted ? "\uD83D\uDD07" : "\uD83D\uDD0A");
        }

        private void updateFooterTrackInfo() {
            if (currentIndex >= 0 && currentIndex < playlist.size()) {
                File file = playlist.get(currentIndex);
                footerSongLabel.setText(formatSongTitle(file));
            } else {
                footerSongLabel.setText("Nothing selected");
            }
        }

        private String formatSongTitle(File file) {
            String name = file.getName();
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex > 0) {
                return name.substring(0, dotIndex);
            }
            return name;
        }

        private void updateRepeatButtonStyle() {
            styleModeButton(repeatButton, playlistRepeatEnabled ? darken(accentColor, 0.55) : "#232932");
        }

        private void updateShuffleButtonStyle() {
            styleIconButton(shuffleButton, shuffleEnabled ? darken(accentColor, 0.55) : "#232932");
        }

        private void styleModeButton(Button button, String background) {
            styleControlButton(button, background, 50, 12, 16, 9, 10);
            button.setStyle(button.getStyle() + "-fx-font-weight: 800;");
        }

        private int getNextIndex(boolean wrapAround) {
            if (playlist.isEmpty()) {
                return -1;
            }

            if (shuffleEnabled) {
                return getRandomOtherIndex();
            }

            int nextIndex = currentIndex + 1;
            if (nextIndex < playlist.size()) {
                return nextIndex;
            }

            if (playlistRepeatEnabled || wrapAround) {
                return 0;
            }
            return -1;
        }

        private int getRandomOtherIndex() {
            if (playlist.isEmpty()) {
                return -1;
            }
            if (playlist.size() == 1) {
                return playlistRepeatEnabled || loopEnabled ? 0 : -1;
            }

            List<Integer> candidates = new ArrayList<>();
            for (int i = 0; i < playlist.size(); i++) {
                if (i != currentIndex) {
                    candidates.add(i);
                }
            }
            Collections.shuffle(candidates, random);
            return candidates.isEmpty() ? -1 : candidates.get(0);
        }
    }
}
