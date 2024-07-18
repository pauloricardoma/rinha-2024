CREATE UNLOGGED TABLE clientes (
	id SERIAL PRIMARY KEY,
	nome VARCHAR(50) NOT NULL,
	limite INTEGER NOT NULL,
	saldo INTEGER NOT NULL DEFAULT 0
);

CREATE UNLOGGED TABLE transacoes (
	id SERIAL PRIMARY KEY,
	cliente_id INTEGER NOT NULL,
	valor INTEGER NOT NULL,
	tipo CHAR(1) NOT NULL,
	descricao VARCHAR(10) NOT NULL,
	realizada_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT fk_clientes_transacoes_id
		FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE INDEX idx_cliente_id ON transacoes (cliente_id);

DO
$$
    BEGIN
        INSERT INTO clientes (nome, limite)
        VALUES
            ('AAA', 1000 * 100),
            ('BBB', 800 * 100),
            ('CCC', 10000 * 100),
            ('DDD', 100000 * 100),
            ('EEE', 5000 * 100);
    END;
$$;

CREATE OR REPLACE FUNCTION debit(
	cliente_id_dt INT,
	valor_dt INT,
	descricao_dt VARCHAR(10))
RETURNS TABLE (
	novo_saldo INT,
	novo_limite INT,
	error BOOL,
	mensagem VARCHAR(20))
AS $$
DECLARE
	saldo_atual int;
	limite_atual int;
BEGIN
	PERFORM pg_advisory_xact_lock(cliente_id_dt);
	SELECT
		saldo,
		limite
	INTO
		saldo_atual,
		limite_atual
	FROM clientes
	WHERE id = cliente_id_dt;

	IF saldo_atual - valor_dt >= limite_atual * -1 THEN
        INSERT INTO transacoes (cliente_id, valor, tipo, descricao)
            VALUES (cliente_id_dt, valor_dt, 'd', descricao_dt);

		UPDATE clientes
		SET saldo = saldo - valor_dt
		WHERE id = cliente_id_dt;

		RETURN QUERY
			SELECT
				saldo,
				limite,
				FALSE,
				'ok'::VARCHAR(20)
			FROM clientes
			WHERE id = cliente_id_dt;
	ELSE
		RETURN QUERY
			SELECT
				saldo,
				limite,
				TRUE,
				'saldo insuficente'::VARCHAR(20)
			FROM clientes
			WHERE id = cliente_id_dt;
	END IF;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION credit(
	cliente_id_dt INT,
	valor_dt INT,
	descricao_dt VARCHAR(10))
RETURNS TABLE (
	novo_saldo INT,
	novo_limite INT,
	error BOOL,
	mensagem VARCHAR(20))
AS $$
BEGIN
	PERFORM pg_advisory_xact_lock(cliente_id_dt);

	INSERT INTO transacoes (cliente_id, valor, tipo, descricao)
	    VALUES (cliente_id_dt, valor_dt, 'c', descricao_dt);

    UPDATE clientes
    SET saldo = saldo + valor_dt
    WHERE id = cliente_id_dt;

    RETURN QUERY
        SELECT
            saldo,
            limite,
            FALSE,
            'ok'::VARCHAR(20)
        FROM clientes
        WHERE id = cliente_id_dt;
END;
$$ LANGUAGE plpgsql;