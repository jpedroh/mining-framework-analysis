package org.agilewiki.pactor;



public interface Request<RESPONSE_TYPE> {
    public abstract Mailbox getMailbox();

    public abstract void send() throws Exception;

    public abstract void reply(final Mailbox source, final ResponseProcessor<RESPONSE_TYPE> responseProcessor) throws Exception;

    public abstract RESPONSE_TYPE pend() throws Exception;

    /**
     * The processRequest is asynchronously invoked by the threads associated with the Requests attached mailbox. The send 
     * methods pushes the Request to the mailbox.
     * 
     * @param responseProcessor The ResponseProcessor contains the Response that is generated from the Request.
     * @throws Exception
     */
    public abstract void processRequest(final ResponseProcessor<RESPONSE_TYPE> responseProcessor) throws Exception;
}