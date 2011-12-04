class Choroby implements Comparable<Choroby> {
	public String URI;
	public String nazwa;

	@Override
	public boolean equals(Object obj) {
		return URI.equals(((Choroby) obj).URI);
	}

	@Override
	public int compareTo(Choroby o) {
		return nazwa.compareTo(o.nazwa);
	}

}
