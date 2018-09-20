package vn.edu.ut.gts.helpers;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public abstract class TextInputValidator implements TextWatcher{
    private final TextView textView;

    public TextInputValidator(TextView textView) {
        this.textView = textView;
    }

    public abstract void validate(TextView textView, String text);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) { }

    @Override
    public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        validate(textView, text);
    }


}
