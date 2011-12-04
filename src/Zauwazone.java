class Zauwazone implements Comparable<Zauwazone> {
	public String URI;
	public String nazwa;

	@Override
	public boolean equals(Object obj) {
		return URI.equals(((Zauwazone) obj).URI);

	}

	@Override
	public int compareTo(Zauwazone o) {
		return nazwa.compareTo(o.nazwa);
	}
}
