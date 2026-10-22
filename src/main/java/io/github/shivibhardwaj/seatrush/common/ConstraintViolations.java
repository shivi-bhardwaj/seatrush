package io.github.shivibhardwaj.seatrush.common;

import org.hibernate.exception.ConstraintViolationException;

public final class ConstraintViolations {

    private ConstraintViolations() {
    }

   public static boolean isViolation(Throwable ex, String constraintName) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof ConstraintViolationException cve) {
                return constraintName.equalsIgnoreCase(cve.getConstraintName());
            }
        }
        return false;
    }
}
