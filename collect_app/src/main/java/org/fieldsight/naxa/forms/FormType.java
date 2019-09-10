package org.fieldsight.naxa.forms;

public enum FormType {
        SURVEY(0),
        GENERAL(1),
        SCHEDULED(2),
        STAGED(3);

        private int code;

        FormType(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }