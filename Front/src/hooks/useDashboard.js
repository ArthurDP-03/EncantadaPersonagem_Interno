import { useState, useEffect } from "react";
import { getDashboard } from "../services/dashboardService";

export function useDashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  async function fetchData() {
    setLoading(true);
    try {
      setData(await getDashboard());
    } catch (e) {
      setError(e?.data?.message || "Erro ao carregar dashboard");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => { fetchData(); }, []);

  return { data, loading, error, refresh: fetchData };
}