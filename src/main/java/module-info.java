/**
 * The java.desktop module is required to use Java AWT.
 */
module jdialogs {
	requires transitive java.desktop;
	exports com.zukadev.dialogs;
}