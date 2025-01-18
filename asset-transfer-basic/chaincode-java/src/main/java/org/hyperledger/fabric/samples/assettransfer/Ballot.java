package org.hyperledger.fabric.samples.assettransfer;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Ballot {

    @Property()
    private final String ballotId;

    @Property()
    private final String ballotMarks;

    @Property()
    private final String candidateOrder;

    @Property()
    private final String ring;

    public String getBallotId() {
        return ballotId;
    }

    public String getBallotMarks() {
        return ballotMarks;
    }

    public String getCandidateOrder() {
        return candidateOrder;
    }

    public String getRing() {
        return ring;
    }

    public Ballot(@JsonProperty("ballotId") final String ballotId,
            @JsonProperty("ballotMarks") final String ballotMarks,
            @JsonProperty("candidateOrder") final String candidateOrder,
            @JsonProperty("ring") final String ring) {
        this.ballotId = ballotId;
        this.ballotMarks = ballotMarks;
        this.candidateOrder = candidateOrder;
        this.ring = ring;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Ballot other = (Ballot) obj;

        return Objects.deepEquals(getBallotId(), other.getBallotId())
                &&
                Objects.deepEquals(getBallotMarks(), other.getBallotMarks())
                &&
                Objects.deepEquals(getCandidateOrder(), other.getCandidateOrder())
                &&
                Objects.deepEquals(getRing(), other.getRing());

    }

    @Override
    public int hashCode() {
        return Objects.hash(getBallotId(), getBallotMarks(), getCandidateOrder(), getRing());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [ballotId=" + ballotId
                + ", ballotMarks=" + ballotMarks + " , candidateOrder=" + candidateOrder + ", ring=" + ring + "]";
    }
}
