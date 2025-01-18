package org.hyperledger.fabric.samples.assettransfer;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

@Contract(name = "basic", info = @Info(title = "Ballot Transfer", description = "The hyperlegendary ballot transfer", version = "0.0.1-SNAPSHOT", license = @License(name = "Apache 2.0 License", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), contact = @Contact(email = "a.transfer@example.com", name = "Capstone Transfer", url = "https://hyperledger.example.com")))
@Default
public final class BallotTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum BallotTransferErrors {
        BALLOT_NOT_FOUND,
        BALLOT_ALREADY_EXISTS
    }

    /**
     * Creates some initial ballots on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        String marks = "[true, false, false]";
        putBallot(ctx, new Ballot("ballot1", marks, "1", "123afdsasdf5"));
        putBallot(ctx, new Ballot("ballot2", marks, "2", "asdfjowqie5"));
        putBallot(ctx, new Ballot("ballot3", marks, "3", "qwiufhaksjdads10"));
        putBallot(ctx, new Ballot("ballot4", marks, "4", "r38ryqfu39210"));
        putBallot(ctx, new Ballot("ballot5", marks, "5", "erg798ih15"));
        putBallot(ctx, new Ballot("ballot6", marks, "6", "q9r8hreog894q15"));

    }

    /**
     * Creates a new ballot on the ledger.
     *
     * @param ctx            the transaction context
     * @param ballotId       the ID of the new ballot
     * @param candidateOrder the candidateOrder of the new ballot
     * @param ring           the ring for the new ballot
     * @return the created ballot
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Ballot CreateBallot(final Context ctx, final String ballotId, final String ballotMarks,
            final String candidateOrder, final String ring) {

        if (BallotExists(ctx, ballotId)) {
            String errorMessage = String.format("Ballot %s already exists", ballotId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BallotTransferErrors.BALLOT_ALREADY_EXISTS.toString());
        }

        return putBallot(ctx, new Ballot(ballotId, ballotMarks, candidateOrder, ring));
    }

    private Ballot putBallot(final Context ctx, final Ballot ballot) {
        // Use Genson to convert the Ballot into string, sort it alphabetically and
        // serialize it into a json string
        String sortedJson = genson.serialize(ballot);
        ctx.getStub().putStringState(ballot.getBallotId(), sortedJson);

        return ballot;
    }

    /**
     * Retrieves a ballot with the specified ID from the ledger.
     *
     * @param ctx      the transaction context
     * @param ballotId the ID of the ballot
     * @return the ballot found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Ballot ReadBallot(final Context ctx, final String ballotId) {
        String ballotJSON = ctx.getStub().getStringState(ballotId);

        if (ballotJSON == null || ballotJSON.isEmpty()) {
            String errorMessage = String.format("Ballot %s does not exist", ballotId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BallotTransferErrors.BALLOT_NOT_FOUND.toString());
        }

        return genson.deserialize(ballotJSON, Ballot.class);
    }

    /**
     * Deletes ballot on the ledger.
     *
     * @param ctx      the transaction context
     * @param ballotId the ID of the ballot being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteBallot(final Context ctx, final String ballotId) {
        if (!BallotExists(ctx, ballotId)) {
            String errorMessage = String.format("Ballot %s does not exist", ballotId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, BallotTransferErrors.BALLOT_NOT_FOUND.toString());
        }

        ctx.getStub().delState(ballotId);
    }

    /**
     * Checks the existence of the ballot on the ledger
     *
     * @param ctx      the transaction context
     * @param ballotId the ID of the ballot
     * @return boolean indicating the existence of the ballot
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean BallotExists(final Context ctx, final String ballotId) {
        String ballotJSON = ctx.getStub().getStringState(ballotId);

        return (ballotJSON != null && !ballotJSON.isEmpty());
    }

    /**
     * Retrieves all ballots from the ledger.
     *
     * @param ctx the transaction context
     * @return array of ballots found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllBallots(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Ballot> queryResults = new ArrayList<>();

        // To retrieve all ballots from the ledger use getStateByRange with empty
        // startKey
        // & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning
        // to end.
        // As another example, if you use startKey = 'ballot0', endKey = 'ballot9' ,
        // then getStateByRange will retrieve ballot with keys between ballot0
        // (inclusive)
        // and ballot9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            Ballot ballot = genson.deserialize(result.getStringValue(), Ballot.class);
            System.out.println(ballot);
            queryResults.add(ballot);
        }

        return genson.serialize(queryResults);
    }
}
