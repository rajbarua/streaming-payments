package com.hz.demo.pmt.domain;

/**
 * A pain.001 message.
 */
public class Payment {
    
        private String id;
        private String debtor;
        private String creditor;
        private Integer amount;
        private String currency;
        private long timestamp;
    
        public Payment() {
        }
    
        public Payment(String id, String debtor, String creditor, Integer amount, String currency
                        , long timestamp) {
            this.id = id;
            this.debtor = debtor;
            this.creditor = creditor;
            this.amount = amount;
            this.currency = currency;
            this.timestamp = timestamp;
        }
    
        public String getId() {
            return id;
        }
    
        public String getDebtor() {
            return debtor;
        }
    
        public String getCreditor() {
            return creditor;
        }
    
        public Integer getAmount() {
            return amount;
        }
    
        public String getCurrency() {
            return currency;
        }
        public long getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
    
        @Override
        public String toString() {
            return "Payment{" +
                    "id='" + id + '\'' +
                    ", debtor='" + debtor + '\'' +
                    ", creditor='" + creditor + '\'' +
                    ", amount='" + amount + '\'' +
                    ", currency='" + currency + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    '}';
        }

}
