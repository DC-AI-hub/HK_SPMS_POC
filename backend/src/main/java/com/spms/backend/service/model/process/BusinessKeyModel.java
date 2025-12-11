package com.spms.backend.service.model.process;

import lombok.Data;

@Data
public class BusinessKeyModel {

    private String prefix;
    private Long sequence;
    private String split;

    /**
     * get the sequence of key
     * example : prefix=REQ,sequence=999,split=-
     *      getSeqStr('a',10); return REQ-aaaaaaa999
     * example2 : prefix=REQ,sequence=999,split=-
     *      getSeqStr('0',10); return REQ-0000000999
     * @param placeholder the placeholder for the sequence
     * @param lengthOfSequence the length of the sequency
     * @return the seq str
     */
    public String getSeqStr(char placeholder, int lengthOfSequence) {
        String seqStr = String.valueOf(sequence);
        
        if (seqStr.length() > lengthOfSequence) {
            throw new IllegalArgumentException("Sequence number " + sequence + 
                " exceeds maximum length of " + lengthOfSequence);
        }
        
        int paddingLength = lengthOfSequence - seqStr.length();
        String padding = String.valueOf(placeholder).repeat(paddingLength);
        
        return prefix + split + padding + seqStr;
    }
}
