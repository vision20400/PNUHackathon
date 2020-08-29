package test;

public enum CellType {

	RECTANGLE,
    TRIANGLE,
    LABEL,
    IMAGE,
    BUTTON,
    TITLEDPANE,
    FILE,
   
    ;


	   public static int toInteger(CellType type) {
	       switch (type) {
	            case RECTANGLE:
	                return 0;
	            case TRIANGLE:
	                return 1;
	            case LABEL:
	                return 2;
	            case IMAGE:
	                return 3;
	            case BUTTON:
	                return 4;
	            case TITLEDPANE:
	                return 5;
	            case FILE:
	                return 6;

	        }
	        return 2;
	    }

	   public static CellType fromInteger(int x) {
	       switch (x) {
	            case 0:
	                return RECTANGLE;
	            case 1:
	                return TRIANGLE;
	            case 2:
	                return LABEL;
	            case 3:
	                return IMAGE;
	            case 4:
	                return BUTTON;
	            case 5:
	                return TITLEDPANE;
	            case 6:
	                return FILE;

	        }
	        return LABEL;
	    }
}