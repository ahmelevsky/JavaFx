package am;

public class DialogConditions {

	public int monthBefore = 12;
	public int monthAfter = 12;
	public int downloadBefore = 100;
	public int downloadsAfter = 100;
	public boolean isLessBefore = false;
	public boolean isLessAfter = true;
	public boolean isAnd = true;
	
	public DialogConditions(int monthBefore, int monthAfter, int downloadBefore, int downloadsAfter,
			boolean isLessBefore, boolean isLessAfter, boolean isAnd) {
		this.monthBefore = monthBefore;
		this.monthAfter = monthAfter;
		this.downloadBefore = downloadBefore;
		this.downloadsAfter = downloadsAfter;
		this.isLessBefore = isLessBefore;
		this.isLessAfter = isLessAfter;
		this.isAnd = isAnd;
	}
	@Override
	public String toString() {
		return "DialogConditions [monthBefore=" + monthBefore + ", monthAfter=" + monthAfter + ", downloadBefore="
				+ downloadBefore + ", downloadsAfter=" + downloadsAfter + ", isLessBefore=" + isLessBefore
				+ ", isLessAfter=" + isLessAfter + ", isAnd=" + isAnd + "]";
	}
	
	
}
