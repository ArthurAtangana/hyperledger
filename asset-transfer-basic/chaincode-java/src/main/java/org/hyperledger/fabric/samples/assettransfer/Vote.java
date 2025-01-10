package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Vote {

    @Property()
    private final String voteId;

    @Property()
    private final String candidateId;

    @Property()
    private final int proof;

    public String getVoteId() {
        return voteId;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public int getProof() {
        return proof;
    }

    public Vote(@JsonProperty("voteId") final String voteId, @JsonProperty("candidateId") final String candidateId,
            @JsonProperty("proof") final int proof) {
        this.voteId = voteId;
        this.candidateId = candidateId;
        this.proof = proof;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Vote other = (Vote) obj;

        return Objects.deepEquals(
                new String[] {getVoteId(), getCandidateId()},
                new String[] {other.getVoteId(), other.getCandidateId()})
                &&
                Objects.deepEquals(
                        new int[] {getProof()},
                        new int[] {other.getProof()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVoteId(), getCandidateId(), getProof());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [voteId=" + voteId
                + ", candidateId="
                + candidateId + ", proof=" + proof + "]";
    }
}
