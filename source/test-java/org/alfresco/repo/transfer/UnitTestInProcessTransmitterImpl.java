package org.alfresco.repo.transfer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.transfer.TransferException;
import org.alfresco.service.cmr.transfer.TransferProgress;
import org.alfresco.service.cmr.transfer.TransferReceiver;
import org.alfresco.service.cmr.transfer.TransferTarget;
import org.alfresco.service.cmr.transfer.TransferVersion;
import org.alfresco.service.transaction.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class delegates transfer service to the transfer receiver without 
 * using any networking.
 *
 * It is used for unit testing the transfer service without requiring two instance 
 * of the repository (and a http server) to be running.
 *
 * @author Mark Rogers
 */
public class UnitTestInProcessTransmitterImpl implements TransferTransmitter
{
    private static final Log log = LogFactory.getLog(UnitTestInProcessTransmitterImpl.class);
    
    private TransferReceiver receiver;
    
    private ContentService contentService;
    private TransactionService transactionService;
    
    public UnitTestInProcessTransmitterImpl(TransferReceiver receiver, ContentService contentService, TransactionService transactionService)
    {
        this.receiver = receiver;
        this.contentService = contentService;
        this.transactionService = transactionService;
    }
    
    public Transfer begin(final TransferTarget target, final String fromRepositoryId, final TransferVersion fromVersion) throws TransferException
    {
        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Transfer>()
        {
            public Transfer execute() throws Throwable
            {
                Transfer transfer = new Transfer();
                String transferId = receiver.start(fromRepositoryId, true, fromVersion);
                transfer.setToVersion(receiver.getVersion());
                transfer.setTransferId(transferId);
                transfer.setTransferTarget(target);
                return transfer;
            }
        }, false, true);
    }

    public void abort(final Transfer transfer) throws TransferException
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Transfer>()
        {
            public Transfer execute() throws Throwable
            {
                String transferId = transfer.getTransferId();
                receiver.cancel(transferId);
                return null;
            }
        }, false, true);
    }

    public void commit(final Transfer transfer) throws TransferException
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Transfer>()
        {
            public Transfer execute() throws Throwable
            {
                String transferId = transfer.getTransferId();
                receiver.commit(transferId);
                return null;
            }
        }, false, true);
    }


    public void prepare(final Transfer transfer) throws TransferException
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                 String transferId = transfer.getTransferId();
                 receiver.prepare(transferId);
                 return null;
            }
        }, false, true);
    }

    public void sendContent(final Transfer transfer, final Set<ContentData> data)
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                String transferId = transfer.getTransferId();
        
                for(ContentData content : data)
                {
                    String contentUrl = content.getContentUrl();
                    String fileName = TransferCommons.URLToPartName(contentUrl);

                    InputStream contentStream = getContentService().getRawReader(contentUrl).getContentInputStream();
                    receiver.saveContent(transferId, fileName, contentStream);
                }
                return null;
            }
        }, false, true);
    }

    public void sendManifest(final Transfer transfer, final File manifest, final OutputStream result) throws TransferException
    {
        transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>()
        {
            public Void execute() throws Throwable
            {
                try
                {
                    String transferId = transfer.getTransferId();
                    FileInputStream fs = new FileInputStream(manifest);
                    receiver.saveSnapshot(transferId, fs);

                    // Now get the requsite
                    try
                    {
                        receiver.generateRequsite(transferId, result);
                        result.close();

                        return null;

                    }
                    catch(IOException ie)
                    {
                        log.error("Error in unit test code: should not get this", ie);
                        return null;
                    }

                }
                catch (FileNotFoundException error)
                {
                    throw new TransferException("test error", error);
                }
            }
        }, false, true);
    }

    public void verifyTarget(TransferTarget target) throws TransferException
    {

    }
    
    public TransferProgress getStatus(final Transfer transfer) throws TransferException
    {
        return transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<TransferProgress>()
        {
            public TransferProgress execute() throws Throwable
            {
                String transferId = transfer.getTransferId();
                return receiver.getStatus(transferId);
            }
        }, false, true);
    }

    public void setReceiver(TransferReceiver receiver)
    {
        this.receiver = receiver;
    }

    public TransferReceiver getReceiver()
    {
        return receiver;
    }

    private ContentService getContentService()
    {
        return contentService;
    }

    public void getTransferReport(Transfer transfer, OutputStream results)
    {
        
        String transferId = transfer.getTransferId();
        
        InputStream is = receiver.getTransferReport(transferId);   
        try 
        {
            BufferedInputStream br = new BufferedInputStream(is);
            byte[] buffer = new byte[1000];
            int i = br.read(buffer);
            while(i > 0)
            {
                results.write(buffer, 0, i);
                i = br.read(buffer);
            }
            results.flush();
            results.close();
        }
        catch(IOException ie)
        {
                log.error("Error in unit test code: should not get this", ie);
                return;
        }
    }
}
