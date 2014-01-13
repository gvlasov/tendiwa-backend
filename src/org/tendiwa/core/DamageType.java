package org.tendiwa.core;

import org.tendiwa.lexeme.Localizable;

public enum DamageType implements Localizable {
	PLAIN {
		@Override
		public String getLocalizationId() {
			return "to hit";
		}
	},
	FIRE {
		@Override
		public String getLocalizationId() {
			return "to burn";
		}
	},
	COLD {
		@Override
		public String getLocalizationId() {
			return "to freeze";
		}
	},
	POISON {
		@Override
		public String getLocalizationId() {
			return "to poison";
		}
	},
	MENTAL {
		@Override
		public String getLocalizationId() {
			return "to psy-attack";
		}
	},
	ELECTRICITY {
		@Override
		public String getLocalizationId() {
			return "to shock";
		}
	},
	ACID {
		@Override
		public String getLocalizationId() {
			return "to burn";
		}
	}
}
