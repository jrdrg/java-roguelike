package roguelike.functionalInterfaces;

import roguelike.DialogResult;

public interface DialogCallback<T> {
	public void setResult(DialogResult<T> result);
}
