package wang.gnim.corejava.security;

public enum Padding {

	NoPadding {
		@Override
		public void pad(byte[] data) {
		}
	},
	
	PKCS5Padding {
		@Override
		public void pad(byte[] data) {
			byte[] input = data;
			if (data.length % 8 != 0) {
				input = new byte[(data.length / 8 + 1) * 8];
				System.arraycopy(data, 0, input, 0, data.length);
			}
		}
	},
	
	ISO10126Padding {
		@Override
		public void pad(byte[] data) {
			byte[] input = data;
			if (data.length % 8 != 0) {
				input = new byte[(data.length / 8 + 1) * 8];
				System.arraycopy(data, 0, input, 0, data.length);
			}
		}
	};

	public abstract void pad(byte[] data);
	
}
