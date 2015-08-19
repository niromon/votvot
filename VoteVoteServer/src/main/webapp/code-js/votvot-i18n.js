var i18n = angular.module('votvot.i18n', ['pascalprecht.translate']);

i18n.config(function ($translateProvider) {
	$translateProvider.translations('en', {
		TITLE: 'Hello',
		YES : 'Yes',
		NO : 'No',
		PROPOSE : 'Propose',
		LANGUAGE : 'Language',
		RESULTS : 'Results',
		SEARCH : 'Search',
		SIGNIN : 'Sign in',
		SIGNUP : 'Sign up',
		SIGNOUT : 'Sign out',
		GO_TO_PAGE : 'Go to question page',
		
		EMAIL : 'Email',
		DATES : 'Dates',
		PASSWORD : 'Password',
		FIRST_NAME : 'First name',
		LAST_NAME : 'Last name',
		BIRTH_DATE : 'Date of birth',
		BIRTH_PLACE : 'Place of birth',
		
		GLOBAL_RESULT : 'Global result',
		PARTICIPATION : 'Participation',
		
		ID_CHECK : 'Identity confirmation',
		POSITIVE_ID : 'This is a good ID',
		NEGATIVE_ID : 'This is a bad ID',
		DONT_KNOW : "I don't know",
		LET_ME_THINK : "Let me think",
		DONT_CARE : 'I do not care',
		DO_YOU_WANT_TO_VOTE_THIS : 'Do you want to vote on this question?'
	});
	$translateProvider.translations('fr', {
		TITLE: 'Bonjour',
		YES:'Oui',
		NO : 'Non',
		PROPOSE : 'Proposer',
		LANGUAGE : 'Langue',
		RESULTS : 'Résultats',
		SEARCH : 'Rechercher',
		SIGNIN : 'Connexion',
		SIGNUP : 'Inscription',
		SIGNOUT : 'Déconnexion',
		GO_TO_PAGE : 'Page de la question',
		
		EMAIL : 'Courriel',
		DATES : 'Dates',
		PASSWORD : 'Mot de passe',
		FIRST_NAME : 'Prénom',
		LAST_NAME : 'Nom',
		BIRTH_DATE : 'Date de naissance',
		BIRTH_PLACE : 'Lieu de naissance',
		
		GLOBAL_RESULT : 'Résultat global',
		PARTICIPATION : 'Participation',
		
		ID_CHECK : "Vérification d'identité",
		POSITIVE_ID : 'Je confirme cette identité',
		NEGATIVE_ID : 'Cette identité est fausse',
		DONT_KNOW : 'Je ne connais pas',
		LET_ME_THINK : "Je vais réfléchir",
		DONT_CARE : "Ne m'intéresse pas",
		DO_YOU_WANT_TO_VOTE_THIS : 'Voulez-vous voter sur cette question?'
	});
	$translateProvider.preferredLanguage('fr');
});