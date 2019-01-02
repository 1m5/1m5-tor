package io.onemfive.tor.client.core.encoders;

public class EncoderException
    extends IllegalStateException
{
	private static final long serialVersionUID = 6589388628939318400L;
	private Throwable cause;

    EncoderException(String msg, Throwable cause)
    {
        super(msg);

        this.cause = cause;
    }

    public Throwable getCause()
    {
        return cause;
    }
}
