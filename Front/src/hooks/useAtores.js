import { useState, useEffect } from "react";
import { getAtores } from "../services/atoresService";

export function useAtores() {
  const [atores, setAtores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState(null);

  useEffect(() => {
    getAtores()
        .then(setAtores)
        .catch(err => {
            console.error("Erro completo:", err);
            setErro(err.message);
        })
        .finally(() => setCarregando(false));
  }, []);

  return { atores, carregando, erro };
}