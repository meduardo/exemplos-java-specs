package exemplos.java.specs.jsr354;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmounts;
import javax.money.MonetaryCurrencies;
import javax.money.MonetaryOperator;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;

import org.javamoney.moneta.FastMoney;
import org.javamoney.moneta.Money;

public class UsandoJavaMoney {

	public static void main( String[] args ) {

		//Classe Currency do Java - desde Java 4.
		for(Currency moeda : Currency.getAvailableCurrencies()){
			System.out.println("Moeda: " + moeda);
		}

		System.out.println("Moeda para o Locale Padrão: " + Currency.getInstance(Locale.getDefault()));

		// -------------------------------------

		// CurrencyUnit - Imutável, ThreadSafe.
		// -> Obtendo uma moeda específica
		CurrencyUnit dollar = MonetaryCurrencies.getCurrency("USD");
		CurrencyUnit real = MonetaryCurrencies.getCurrency("BRL");

		// -> Obtendo uma moeda, com base na localição - Locale.
		CurrencyUnit euro = MonetaryCurrencies.getCurrency(Locale.FRANCE);
		CurrencyUnit moedaPadrao = MonetaryCurrencies.getCurrency(Locale.getDefault());

		System.out.println("Moeda padrão: " + moedaPadrao);
		System.out.println("Real: " + real);
		System.out.println("Dollar: " + dollar);
		System.out.println("Euro: " + euro);		
		
		// MonetaryAmount 
		// -> Obtido por uma Fábrica (Factory)
		MonetaryAmount valorEmReais = MonetaryAmounts.getDefaultAmountFactory()
				.setNumber(new BigDecimal("1500.55"))
				.setCurrency("BRL")
				.create();
		System.out.println("Reais: " + valorEmReais);

		// -> Metodos uteis com base no M.Amount
		//  
		BigDecimal valor = valorEmReais.getNumber().numberValue(BigDecimal.class);
		System.out.println("Valor como BigDecimal: " + valor);
		
		valorEmReais.isNegativeOrZero();
		valorEmReais.isNegative();
		valorEmReais.isPositiveOrZero();
		valorEmReais.isPositive();
		valorEmReais.isZero();
		valorEmReais.abs();
		valorEmReais.negate();
		valorEmReais.isGreaterThan(valorEmReais);
		valorEmReais.isLessThan(valorEmReais);

		// -> Algumas classes da implementação padrão podem ser usadas, por ex.:
		MonetaryAmount valorEmDollar = Money.of(new BigDecimal("100"), dollar);
		valorEmDollar = FastMoney.of(new BigDecimal("100"), dollar); 

		System.out.println("Dólares: " + valorEmDollar);


		// Operacoes com valores monetarios
		// -> Calculos
		valorEmReais.divide(2);
		valorEmReais.multiply(3);
		valorEmReais.add(valorEmReais);

		// Somar Reais em Dólares não pode ser feito! Exception!
		//valorEmReais.add(valorEmDollar);

		// -> Arredondamentos

		// -> Criando Operacoes Customizadas
		// Criando um Operador com recurso de Lambda do Java 8
		// Com o uso do Java 7, seria possível criar uma Classe propria, 
		// e implementar o que for desejado, pelo método "apply"
		
		double desconto = 0.2 ;
		
		MonetaryOperator operacaoDeDesconto = (MonetaryAmount valorEmMoeda) -> valorEmMoeda.multiply(desconto);
		MonetaryAmount comDesconto = valorEmReais.with(operacaoDeDesconto); 
		System.out.println("Valor com desconto: " + comDesconto);
		
		comDesconto = valorEmReais.with(Discount.of(desconto)); 
		System.out.println("Minha própria classe de desconto:" + comDesconto);
		
		// Formatacao e apresentacao

		// Formata com base no Locale
		MonetaryAmountFormat formatoPadrao = MonetaryFormats.getAmountFormat(Locale.getDefault());
		MonetaryAmountFormat formatoEstadosUnidos = MonetaryFormats.getAmountFormat(Locale.US);

		String formatadoPadraoEUA = formatoEstadosUnidos.format(valorEmReais);
		System.out.println("Formato EUA: " + formatadoPadraoEUA);

		String formatadoPadraoBR = formatoPadrao.format(valorEmReais);
		System.out.println("Formato BR: " + formatadoPadraoBR);

		// Também é possível obter o valor com base na String de exibição do mesmo
		MonetaryAmount obtidoComFormatoBR = formatoPadrao.parse("BRL 1.500,55");
		System.out.println("Valor obtido com base na String de formato: " + obtidoComFormatoBR );

		// Criando formato proprio
		

		// Trabalhando com Câmbio e conversões

	}

	//Exemplo de uma classe própria para representar um Desconto
	private static final class Discount implements MonetaryOperator{
		private double percentage = 0;

		private Discount(double percentage) {
			setPorcentagem(percentage);
		}

		public static MonetaryOperator of(double percentage){
			return new Discount(percentage);
		}
		
		private void setPorcentagem(double percentage){
			this.percentage = percentage/100;
		}

		@Override
		public MonetaryAmount apply(MonetaryAmount t) {
			return percentage <= 0 ? t : t.multiply(percentage);
		}
	}
}
