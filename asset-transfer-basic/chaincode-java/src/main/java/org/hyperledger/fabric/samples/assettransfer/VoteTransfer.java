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

@Contract(name = "basic", info = @Info(title = "Vote Transfer", description = "The hyperlegendary vote transfer", version = "0.0.1-SNAPSHOT", license = @License(name = "Apache 2.0 License", url = "http://www.apache.org/licenses/LICENSE-2.0.html"), contact = @Contact(email = "a.transfer@example.com", name = "Adrian Transfer", url = "https://hyperledger.example.com")))
@Default
public final class VoteTransfer implements ContractInterface {

    private final Genson genson = new Genson();

    private enum VoteTransferErrors {
        VOTE_NOT_FOUND,
        VOTE_ALREADY_EXISTS
    }

    /**
     * Creates some initial votes on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void InitLedger(final Context ctx) {
        putVote(ctx, new Vote("vote1", "1", 5));
        putVote(ctx, new Vote("vote2", "2", 5));
        putVote(ctx, new Vote("vote3", "3", 10));
        putVote(ctx, new Vote("vote4", "4", 10));
        putVote(ctx, new Vote("vote5", "5", 15));
        putVote(ctx, new Vote("vote6", "6", 15));

    }

    /**
     * Creates a new vote on the ledger.
     *
     * @param ctx         the transaction context
     * @param voteId      the ID of the new vote
     * @param candidateId the candidateId of the new vote
     * @param proof       the proof for the new vote
     * @return the created vote
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public Vote CreateVote(final Context ctx, final String voteId, final String candidateId, final int proof) {

        if (VoteExists(ctx, voteId)) {
            String errorMessage = String.format("Vote %s already exists", voteId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, VoteTransferErrors.VOTE_ALREADY_EXISTS.toString());
        }

        return putVote(ctx, new Vote(voteId, candidateId, proof));
    }

    private Vote putVote(final Context ctx, final Vote vote) {
        // Use Genson to convert the Vote into string, sort it alphabetically and
        // serialize it into a json string
        String sortedJson = genson.serialize(vote);
        ctx.getStub().putStringState(vote.getVoteId(), sortedJson);

        return vote;
    }

    /**
     * Retrieves a vote with the specified ID from the ledger.
     *
     * @param ctx    the transaction context
     * @param voteID the ID of the vote
     * @return the vote found on the ledger if there was one
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public Vote ReadVote(final Context ctx, final String voteID) {
        String voteJSON = ctx.getStub().getStringState(voteID);

        if (voteJSON == null || voteJSON.isEmpty()) {
            String errorMessage = String.format("Vote %s does not exist", voteID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, VoteTransferErrors.VOTE_NOT_FOUND.toString());
        }

        return genson.deserialize(voteJSON, Vote.class);
    }

    /**
     * Deletes vote on the ledger.
     *
     * @param ctx    the transaction context
     * @param voteID the ID of the vote being deleted
     */
    @Transaction(intent = Transaction.TYPE.SUBMIT)
    public void DeleteVote(final Context ctx, final String voteID) {
        if (!VoteExists(ctx, voteID)) {
            String errorMessage = String.format("Vote %s does not exist", voteID);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, VoteTransferErrors.VOTE_NOT_FOUND.toString());
        }

        ctx.getStub().delState(voteID);
    }

    /**
     * Checks the existence of the vote on the ledger
     *
     * @param ctx    the transaction context
     * @param voteID the ID of the vote
     * @return boolean indicating the existence of the vote
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public boolean VoteExists(final Context ctx, final String voteID) {
        String voteJSON = ctx.getStub().getStringState(voteID);

        return (voteJSON != null && !voteJSON.isEmpty());
    }

    /**
     * Retrieves all votes from the ledger.
     *
     * @param ctx the transaction context
     * @return array of votes found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllVotes(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Vote> queryResults = new ArrayList<>();

        // To retrieve all votes from the ledger use getStateByRange with empty startKey
        // & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning
        // to end.
        // As another example, if you use startKey = 'vote0', endKey = 'vote9' ,
        // then getStateByRange will retrieve vote with keys between vote0 (inclusive)
        // and vote9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result : results) {
            Vote vote = genson.deserialize(result.getStringValue(), Vote.class);
            System.out.println(vote);
            queryResults.add(vote);
        }

        return genson.serialize(queryResults);
    }
}
